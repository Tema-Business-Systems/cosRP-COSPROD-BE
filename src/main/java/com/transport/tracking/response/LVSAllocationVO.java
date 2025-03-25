package com.transport.tracking.response;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
public class LVSAllocationVO {


      private String prhnum;
      private int lineno;
      private String site;
      private float count;
      private String prod;
      private float qty;
      private String lot;
      private int stagflg;
	  private String vrnum;

}
