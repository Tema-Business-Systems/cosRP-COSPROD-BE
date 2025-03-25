package com.transport.tracking.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "XTMSG2ADDDOCS")
public class OpenDocsRoutes implements Serializable {

      @Column(name= "CODE")
      private String code;
      @Column(name= "DRIVERNAME")
      private String driverName;
      @Column(name= "ROUTESTATUS")
      private String routeStatus;
      @Column(name= "TRIPS")
      private int trips;

      @Column(name= "TOTALOBJECT")
      private String totalObject;
      @Column(name= "STOPS")
      private int stops;
      @Column(name= "DOCDATE")
      private Date docdate;
      @Id
      @Column(name= "TRIPCODE")
      private String tripCode;
      @Column(name= "SITE")
      private String site;
      @Column(name= "lock")
      private int lock;

      @Column(name= "driverId")
      private String driverId;
      @Column(name= "startTime")
      private String startTime;

      @Column(name= "endTime")
      private String endTime;

      @Column(name= "PER_CAPACTY")
      private Double per_capacity;

      @Column(name= "PER_VOLUME")
      private Double per_volume;

      @Column(name= "TOT_CAPACTY")
      private String tot_capacity;

      @Column(name= "DOC_CAPACTY")
      private String doc_capacity;

      @Column(name= "UOM_CAPACTY")
      private String uom_capacity;

      @Column(name= "TOT_VOLUME")
      private String tot_volume;

      @Column(name= "DOC_VOLUME")
      private String doc_volume;

      @Column(name= "UOM_VOLUME")
      private String uom_volume;



}
