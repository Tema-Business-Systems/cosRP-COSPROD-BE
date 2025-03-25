package com.transport.tracking.model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "X10CSOH")
public class SorderList {


      @Column(name= "UPDTICK_0")
      private int updtick;
      @Column(name= "XTLVSNUM_0")
      private String lvsno;
      @Column(name= "XTVRNUM_0")
      private String vrnum;
      @Column(name= "XTSOHNUM_0")
      private String sohnum;
      @Column(name= "XTSDHNUM_0")
      private String sdhnum;
      @Column(name= "XSEQ_0")
      private int seqno;
      @Column(name= "XTPTHNUM_0")
      private String pthnum;
      @Column(name= "XTLINENUM_0")
      private int lineno;
      @Column(name= "CREDATTIM_0")
      private Date credattim;
      @Column(name= "UPDDATTIM_0")
      private Date upddattim;
      @Column(name= "AUUID_0")
      private byte[] auuid;
      @Column(name= "CREUSR_0")
      private String creusr;
      @Column(name= "UPDUSR_0")
      private String updusr;
      @Id
      @Column(name= "ROWID")
      private BigDecimal rowid;

      public SorderList() {
      }

      /*@ManyToMany(cascade = { CascadeType.ALL })
      @JoinTable(
              name = "XX10CUSRROL",
              joinColumns = { @JoinColumn(name = "XUSER_0") },
              inverseJoinColumns = { @JoinColumn(name = "XROLE_0") }
      )
      private Set<Role> roles = new HashSet<>();*/
}
