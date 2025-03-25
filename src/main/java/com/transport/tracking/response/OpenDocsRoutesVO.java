package com.transport.tracking.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenDocsRoutesVO {

    private String code;
    private String driverName;
    private String routeStatus;
    private int trips;
    private Object totalObject;
    private int stops;
    private Date docdate;
    private String tripCode;
    private String site;
    private int lock;
    private String driverId;
    private String startTime;
    private String endTime;
    private Double per_capacity;
    private Double per_volume;
    private String tot_capacity;
    private String doc_capacity;
    private String uom_capacity;
    private String tot_volume;
    private String doc_volume;
    private String uom_volume;


}


