package com.transport.tracking.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehiclePanelVO {

    public List<VehicleVO> vehicles = new ArrayList<>();
    public List<EquipmentVO> equipments = new ArrayList<>();
    public List<TrailVO> trails = new ArrayList<>();
    public List<DriverVO> drivers = new ArrayList<>();
    public List<Map<String, Object>> customers;
}
