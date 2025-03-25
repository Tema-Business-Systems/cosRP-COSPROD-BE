package com.transport.tracking.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "X1CPICKDET")
public class LVSAllocation {

      @Column(name= "PRHNUM_0")
      private String prhnum;
      @Column(name= "PRELIN_0")
      private int lineno;
      @Column(name= "STOFCY_0")
      private String site;
      @Column(name= "STOCOU_0")
      private float count;
      @Column(name= "ITMREF_0")
      private String prod;
      @Column(name= "QTY_0")
      private float qty;
      @Column(name= "LOT_0")
      private String lot;
      @Column(name= "STAFLAG_0")
      private int stagflg;
      @Id
      @Column(name= "ROWID")
      private int rowid;
}
