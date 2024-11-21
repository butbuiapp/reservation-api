package miu.asd.reservationmanagement.service;

import miu.asd.reservationmanagement.model.NailService;

import java.util.List;

public interface NailServiceService {
    NailService saveService(NailService service);
    void updateService(Integer id, NailService service);
    void deleteServiceById(Integer id);
    List<NailService> getAllServices();
    NailService getServiceById(Integer id);
}
