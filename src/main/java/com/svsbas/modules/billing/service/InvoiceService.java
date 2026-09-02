package com.svsbas.modules.billing.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.svsbas.common.exception.BadRequestException;
import com.svsbas.common.exception.ResourceNotFoundException;
import com.svsbas.common.util.ReferenceGenerator;
import com.svsbas.modules.billing.dto.InvoiceResponse;
import com.svsbas.modules.billing.dto.PaymentProcessRequest;
import com.svsbas.modules.billing.entity.*;
import com.svsbas.modules.billing.repository.InvoiceItemRepository;
import com.svsbas.modules.billing.repository.InvoiceRepository;
import com.svsbas.modules.booking.entity.BookingServiceJunction;
import com.svsbas.modules.booking.entity.BookingStatus;
import com.svsbas.modules.booking.entity.ServiceBooking;
import com.svsbas.modules.booking.repository.ServiceBookingRepository;
import com.svsbas.modules.breakdown.entity.BreakdownRequest;
import com.svsbas.modules.breakdown.entity.BreakdownStatus;
import com.svsbas.modules.breakdown.repository.BreakdownRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository itemRepository;
    private final ServiceBookingRepository bookingRepository;
    private final BreakdownRequestRepository breakdownRepository;
    private final CostCalculationService costCalculationService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          InvoiceItemRepository itemRepository,
                          ServiceBookingRepository bookingRepository,
                          BreakdownRequestRepository breakdownRepository,
                          CostCalculationService costCalculationService) {
        this.invoiceRepository = invoiceRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.breakdownRepository = breakdownRepository;
        this.costCalculationService = costCalculationService;
    }

    @Transactional
    public InvoiceResponse generateForBooking(Long bookingId, BigDecimal laborHours, BigDecimal hourlyRate) {
        ServiceBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Booking", "id", bookingId));

        if (invoiceRepository.findByBookingId(bookingId).isPresent()) {
            return new InvoiceResponse(invoiceRepository.findByBookingId(bookingId).get());
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceRef(ReferenceGenerator.generateInvoiceRef());
        invoice.setBooking(booking);
        invoice.setTaxRate(CostCalculationService.STANDARD_TAX_RATE);

        BigDecimal partsCost = BigDecimal.ZERO;
        BigDecimal laborCost = BigDecimal.ZERO;

        if (laborHours != null && laborHours.compareTo(BigDecimal.ZERO) > 0) {
            if (hourlyRate == null) hourlyRate = BigDecimal.valueOf(450.00); // Standard rate ₹450/hr
            laborCost = laborHours.multiply(hourlyRate);
            InvoiceItem laborItem = new InvoiceItem(invoice, ItemType.LABOR, "Technician Workshop Labor (" + laborHours + " hrs)", 1, laborCost);
            invoice.getItems().add(laborItem);
        }

        // Add packages from booking
        for (BookingServiceJunction bs : booking.getServices()) {
            InvoiceItem svcItem = new InvoiceItem(invoice, ItemType.SERVICE_CHARGE, bs.getCatalogItem().getServiceName(), 1, bs.getAgreedPrice());
            invoice.getItems().add(svcItem);
            partsCost = partsCost.add(bs.getAgreedPrice());
        }

        invoice.setTotalLaborCost(laborCost);
        invoice.setTotalPartsCost(partsCost);

        BigDecimal subtotal = laborCost.add(partsCost);
        BigDecimal tax = costCalculationService.calculateTax(subtotal, invoice.getTaxRate());
        BigDecimal total = costCalculationService.calculateTotal(subtotal, tax);

        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);

        booking.setStatus(BookingStatus.INVOICED);
        Invoice saved = invoiceRepository.save(invoice);
        return new InvoiceResponse(saved);
    }

    @Transactional
    public InvoiceResponse generateForBreakdown(Long breakdownId, BigDecimal roadsideFee) {
        BreakdownRequest breakdown = breakdownRepository.findById(breakdownId)
                .orElseThrow(() -> new ResourceNotFoundException("Breakdown request", "id", breakdownId));

        if (invoiceRepository.findByBreakdownId(breakdownId).isPresent()) {
            return new InvoiceResponse(invoiceRepository.findByBreakdownId(breakdownId).get());
        }

        if (roadsideFee == null) roadsideFee = BigDecimal.valueOf(799.00); // Standard emergency dispatch fee

        Invoice invoice = new Invoice();
        invoice.setInvoiceRef(ReferenceGenerator.generateInvoiceRef());
        invoice.setBreakdown(breakdown);
        invoice.setTaxRate(CostCalculationService.STANDARD_TAX_RATE);
        invoice.setTotalLaborCost(roadsideFee);

        InvoiceItem dispatchItem = new InvoiceItem(invoice, ItemType.ROADSIDE_SURCHARGE, "Emergency Roadside Dispatch & Rapid Response", 1, roadsideFee);
        invoice.getItems().add(dispatchItem);

        BigDecimal tax = costCalculationService.calculateTax(roadsideFee, invoice.getTaxRate());
        BigDecimal total = costCalculationService.calculateTotal(roadsideFee, tax);

        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);

        Invoice saved = invoiceRepository.save(invoice);
        return new InvoiceResponse(saved);
    }

    @Transactional
    public InvoiceResponse addItem(Long invoiceId, ItemType itemType, String description, Integer qty, BigDecimal unitPrice) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        InvoiceItem item = new InvoiceItem(invoice, itemType, description, qty, unitPrice);
        invoice.getItems().add(item);

        if (itemType == ItemType.LABOR) {
            invoice.setTotalLaborCost(invoice.getTotalLaborCost().add(item.getSubtotal()));
        } else {
            invoice.setTotalPartsCost(invoice.getTotalPartsCost().add(item.getSubtotal()));
        }

        BigDecimal subtotal = invoice.getTotalLaborCost().add(invoice.getTotalPartsCost());
        BigDecimal tax = costCalculationService.calculateTax(subtotal, invoice.getTaxRate());
        BigDecimal total = costCalculationService.calculateTotal(subtotal, tax);

        invoice.setTaxAmount(tax);
        invoice.setTotalAmount(total);

        return new InvoiceResponse(invoiceRepository.save(invoice));
    }

    @Transactional
    public InvoiceResponse processPayment(Long invoiceId, PaymentProcessRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        if (invoice.getPaymentStatus() == PaymentStatus.PAID) {
            throw new BadRequestException("Invoice is already paid");
        }

        invoice.setPaymentStatus(PaymentStatus.PAID);
        invoice.setPaymentMode(request.getPaymentMode());
        invoice.setPaidAt(LocalDateTime.now());

        if (invoice.getBooking() != null) {
            invoice.getBooking().setStatus(BookingStatus.COMPLETED);
        }
        if (invoice.getBreakdown() != null) {
            invoice.getBreakdown().setStatus(BreakdownStatus.RESOLVED);
        }

        return new InvoiceResponse(invoiceRepository.save(invoice));
    }

    public InvoiceResponse getInvoiceById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
        return new InvoiceResponse(invoice);
    }

    public byte[] generatePdf(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", invoiceId));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Font subFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        Font normal = FontFactory.getFont(FontFactory.HELVETICA, 10);

        document.add(new Paragraph("Smart Vehicle Service & Breakdown Assistance (SVS-BAS)", titleFont));
        document.add(new Paragraph("Official Tax Invoice / Receipt", subFont));
        document.add(new Paragraph("Invoice Reference: " + invoice.getInvoiceRef(), normal));
        document.add(new Paragraph("Status: " + invoice.getPaymentStatus() + (invoice.getPaidAt() != null ? " (Paid: " + invoice.getPaidAt() + ")" : ""), normal));
        document.add(new Paragraph("--------------------------------------------------------------------------------"));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.addCell(new PdfPCell(new Phrase("Description", subFont)));
        table.addCell(new PdfPCell(new Phrase("Qty", subFont)));
        table.addCell(new PdfPCell(new Phrase("Rate (INR)", subFont)));
        table.addCell(new PdfPCell(new Phrase("Subtotal (INR)", subFont)));

        for (InvoiceItem item : invoice.getItems()) {
            table.addCell(new Phrase(item.getDescription(), normal));
            table.addCell(new Phrase(String.valueOf(item.getQuantity()), normal));
            table.addCell(new Phrase(item.getUnitPrice().toString(), normal));
            table.addCell(new Phrase(item.getSubtotal().toString(), normal));
        }

        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total Labor: INR " + invoice.getTotalLaborCost(), normal));
        document.add(new Paragraph("Total Parts & Packages: INR " + invoice.getTotalPartsCost(), normal));
        document.add(new Paragraph("GST / Tax (18%): INR " + invoice.getTaxAmount(), normal));
        Paragraph totalP = new Paragraph("Grand Total: INR " + invoice.getTotalAmount(), titleFont);
        totalP.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalP);

        document.close();
        return out.toByteArray();
    }
}
