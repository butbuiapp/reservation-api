package miu.asd.reservationmanagement.service.impl;

import lombok.RequiredArgsConstructor;
import miu.asd.reservationmanagement.dto.request.InvoiceRequestDto;
import miu.asd.reservationmanagement.repository.InvoiceRepository;
import miu.asd.reservationmanagement.service.InvoiceService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceRepository invoiceRepository;

    @Override
    public void saveInvoice(InvoiceRequestDto invoiceRequestDto) {

    }
}
