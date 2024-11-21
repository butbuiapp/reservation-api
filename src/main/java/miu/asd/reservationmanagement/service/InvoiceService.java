package miu.asd.reservationmanagement.service;

import miu.asd.reservationmanagement.dto.request.InvoiceRequestDto;

public interface InvoiceService {
    void saveInvoice(InvoiceRequestDto invoiceRequestDto);
}
