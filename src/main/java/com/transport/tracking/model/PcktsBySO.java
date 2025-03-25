package com.transport.tracking.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "XTMSPCKSO")
public class PcktsBySO {


    @Column(name= "PRHNUM_0")
    private String prhnum;
    @Column(name= "ORINUM_0")
    private String sorder;
    @Id
    @Column(name= "ROWID")
    private String rowid;


}
