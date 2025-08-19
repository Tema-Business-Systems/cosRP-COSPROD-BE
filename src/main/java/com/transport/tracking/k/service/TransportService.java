package com.transport.tracking.k.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tracking.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.transport.tracking.repository.*;
import com.transport.tracking.response.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.util.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import java.util.TimeZone;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TransportService {

    @Autowired
    private VehicleRepository vehicleRepository;
    //added for VR Screen by Ashok
    @Autowired
    private VehRouteRepository vehRouteRepository;

    //added for VR screen by Ashok
    @Autowired
    private VehRouteDetailRepository vehRouteDetailRepository;

    //added for user-site  screen by Ashok
    @Autowired
    private UserSiteRepository userSiteRepository;

    @Autowired
    private DocDsRepository docDsRepository;

    @Autowired
    private SorderListRepository sorderListRepository;

    //added for VR screen by Ashok
    @Autowired
    private LoadVehStockRepository loadVehStockRepository;

    @Autowired
    private TexclobRepository texclobRepository;

   @Autowired
    private RouteCodeRepository routeCodeRepository;



    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TrailRepository trailRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private DropsRepository dropsRepository;

    @Autowired
    private DlvyBolRepository dlvyBolRepository;

    @Autowired
    private DocsRepository docsRepository;

    @Autowired
    private LoadMsgRepository loadMsgRepository;

    @Autowired
    private PcktsBySORepository pcktsBySORepository;

    @Autowired
    private PickupRepository pickupRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private CacheService cacheService;

    @Value("${db.schema}")
    private String dbSchema;
    //private String dbSchema = "tbs.TMSBURBAN";

   // private String VR_NUMBER = "WVR-{0}-{1}-0{2}";
   // private String TRIP_NUMBER = "WVR-{0}-{1}-{2}";
    private String LVS_NUMBER = "{0}{1}{2}{3}{4}";
    private String TEXT_CLOB = "{0}{1}";
   // private String SINGLE_DIGIT_VR_NUMBER = "WVR-{0}-{1}-00{2}";

    private String VR_NUMBER = "XVR-{0}-{1}-0{2}";
	   private String SELECT_LVS_QUERY_2 = "SELECT * FROM {0}.{1} where VCRNUM_0 = ''{2}''";
  private String DELTE_TRIP_QUERY_2 = "delete from {0}.{1} where VCRNUM_0 = ''{2}''";
    private String TRIP_NUMBER = "XVR-{0}-{1}-{2}";
    private String SINGLE_DIGIT_VR_NUMBER = "XVR-{0}-{1}-00{2}";
    private String UPDATE_QUERY = "update {0}.{1} set {2} = ''{5}'' , {3} = ''{5}'' where {4} = ''{6}''";
    private String  UPDATE_DOC_AFTER_PTHEADER = "update {0}.{1} set {2} = ''{3}''  where {4} = ''{5}''";
    private String UPDATE_NextTrip_QUERY = "update {0}.{1} set {2} = ''{4}'' where  {3} = ''{5}''";
    private String UPDATE_SINGLE_QUERY = "update {0}.{1} set {2} = ''{4}'' where {3} = ''{5}''";
    private String UPDATE_SINGLE_QUERY_INT = "update {0}.{1} set {2} = {4} where {3} = ''{5}''";
    private String UPDATE_PTHEADER_QUERY = "update {0}.{1} set {2} = ''{4}'' where {3} = ''{5}''";
    private String UPDATE_SINGLE_QUERY_MULTIPLE_COND = "update {0}.{1} set {2} = ''{3}'' where {4} = ''{5}'' AND {6} = ''{7}'' and {8} >= ''{9}''";
     private String UPDATE_doc_QUERY_AT_TripCreation = "update {0}.{1} set {2} = ''{6}'' , {3} = ''{7}'', {5} = {10}, {11} = ''{8}'',{12}={13},{14} = {15},{16} = ''{8}'',{17} = ''{18}''  where {4} = ''{9}''";
	private String UPDATE_doc_QUERY = "update {0}.{1} set {2} = ''{12}'' , {3} = ''{13}'', {4} = ''{14}'', {5} = ''{15}'',{7} = ''{17}'',{8} =''{29}'',{9} =''{30}'',{10} =''{19}'',{11} =''{20}'',{21}=''{22}'',{23}=''{24}'',{25}=''{26}'',{27} = {28}, {31} = ''{32}'' where {6} = ''{16}''";
    private String DELTE_TRIP_QUERY = "delete from {0}.{1} where XNUMPC_0 = ''{2}''";
    private String DELTE_SINGLETRIP_QUERY = "delete from {0}.{1} where TRIPCODE = ''{2}''";
    private String SELECT_TRIP_QUERY = "SELECT * FROM {0}.{1} where XNUMPC_0 = ''{2}''";

    private String SELECT_SORDERH_QUERY = "SELECT * FROM {0}.{1} where XTVRNUM_0 = ''{2}''";
    private String DELTE_SORDERH_QUERY = "delete from {0}.{1} where XTVRNUM_0 = ''{2}''";

  private String SELECT_SORDERH_QUERY2 = "SELECT * FROM {0}.{1} where XTVRNUM_0 = ''{2}'' AND {3} = ''{4}'' ";
    private String UPDATE_SORDERH_QUERY = "UPDATE  {0}.{1} SET XTPTHNUM_0 = ''{5}'' where XTVRNUM_0 = ''{2}'' AND {3} = ''{4}'' ";
  private  String UPDATE_TRIP_AFTER_DOC_DELETE = "update {0}.{1} SET optistatus = ''Open'', {2} = ''{3}'', {4} = {5} , {6} = {7}, lock = '0' WHERE {8} = ''{9}'' ";
 private String UPDATE_SINGLE_QUERY_2_CONDITIONS = "update {0}.{1} set {2} = ''{3}'',{4} = ''{5}''  where {6} = ''{7}''";



    // private String Select_PTHeader_Query = "SELECT "
   private String DELTE_LVS_QUERY = "delete from {0}.{1} where XVRSEL_0 = ''{2}''";
    private String SELECT_LVS_QUERY = "SELECT * FROM {0}.{1} where XVRSEL_0 = ''{2}''";
    private String SELECT_DOC_CHECK_QUERY = "SELECT * FROM {0}.{1} where XX10C_NUMPC_0 = ''{2}''";
    private String SELECT_SO_FROM_PICKTICKET_CHECK_QUERY = "SELECT ORINUM_0 FROM {0}.{1} where PRHNUM_0 = ''{2}'' and ORITYP_0 =1 ";
    private String SELECT_SINGLETRIP_QUERY = "SELECT * FROM {0}.{1} where TRIPCODE = ''{2}''";
    private String SELECT_TRIPS_GRTTHAN_QUERY = "SELECT TRIPCODE FROM {0}.{1} where TRIPS >= {2} and CODE = ''{3}'' and DOCDATE= ''{4}''";
    private String UPDATE_doc_QUERY_AFTER_VR_DELETION = "update {0}.{1} SET XX10C_NUMPC_0 = ''{3}'',{5} = ''{3}'',{6} = ''{3}'',{7} = ''{3}'',XDLV_STATUS_0 = {4},{8} =''{3}'',{9} =''{3}'',{10} = ''{3}'',{11}=''{3}'',{12}=''{3}'',{13}=''{3}'',{14}=''{3}''  where XX10C_NUMPC_0 =  ''{2}'' ";
private String UPDATE_doc_QUERY_AFTER_doc_DELETION = "update {0}.{1} SET XX10C_NUMPC_0 = ''{3}'',{5} = ''{3}'',{6} = ''{3}'',{7} = ''{3}'',XDLV_STATUS_0 = {4},{8} =''{3}'',{9} =''{3}'',{10} = ''{3}'',{11}=''{3}'',{12}=''{3}'',{13}=''{3}'',{14}=''{3}''  where {15} =  ''{2}'' ";
    private String UPDATE_doc_QUERY_AFTER_TRIP_DELETION = "update {0}.{1} SET XX10C_NUMPC_0 = ''{3}'',{5} = ''{3}'',{6} = ''{3}'',{7} = ''{3}'',XDLV_STATUS_0 = {4},{8} =''{3}'',{9} =''{3}'',{10} = ''{3}'',{12}=''{3}'',{13}=''{3}'',{14}=''{3}''  where XX10C_NUMPC_0 =  ''{2}'' ";
    private static SimpleDateFormat tripFormat = new SimpleDateFormat("MMddYYYY");

   private String SELECT_ALLOCATION_QUERY = "SELECT * FROM {0}.{1} where PRHNUM_0 = ''{2}'' and PRELIN_0 = {3} ";

    private String DELTE_ALLOCATED_QUERY = "delete from {0}.{1} where PRHNUM_0 = ''{2}'' and PRELIN_0 = {3} ";

private static final String SELECT_ALLOCATION_QUERY_UPD = "SELECT * FROM {schema}.{table} WHERE PRHNUM_0 = :prhnum AND PRELIN_0 = :prelin";

private static final String DELTE_ALLOCATED_QUERY_UPD = "delete from  {schema}.{table} WHERE PRHNUM_0 = :prhnum AND PRELIN_0 = :prelin";




    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


    private static final String TRIPS_CACHE = "trips";

    public List<VehicleVO> listTransports(String site, Boolean active) {
        log.info("Transport service is loaded...");
        List<Vehicle> vehicleList = null;

        if (!StringUtils.isEmpty(site)) {
            vehicleList = vehicleRepository.findByFcy(site);
        } else {
            vehicleList = new ArrayList<>();
            Iterator<Vehicle> iterator = vehicleRepository.findAll().iterator();
            while (iterator.hasNext()) {
                vehicleList.add(iterator.next());
            }
        }

        if (!CollectionUtils.isEmpty(vehicleList)) {
            return vehicleList.parallelStream().map(a -> this.convert(a))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private VehicleVO convert(Vehicle vehicle) {
        VehicleVO vehicleVO = new VehicleVO();
        BeanUtils.copyProperties(vehicle, vehicleVO);
        return vehicleVO;
    }

    public List<SiteVO> getSites() {
        List<Facility> facilities = facilityRepository.findByFcyNumberOrderByFcynamAsc(2);
        List<SiteVO> list = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Facility facility : facilities) {
            try {
                if (!StringUtils.isEmpty(facility.getFcy().trim()) && !ids.contains(facility.getFcy())) {
                    SiteVO siteVO = new SiteVO();
                    siteVO.id = facility.getFcy();
                    siteVO.value = facility.getFcynam();
                    if (!StringUtils.isEmpty(facility.getXx10c_geox()) && !StringUtils.isEmpty(facility.getXx10c_geoy())) {
                        try {
                            siteVO.lat = Double.parseDouble(facility.getXx10c_geoy());
                            siteVO.lng = Double.parseDouble(facility.getXx10c_geox());
                        } catch (Exception e) {
                        }
                    }
                    list.add(siteVO);
                }
                ids.add(facility.getFcy());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;

    }



    public List<SiteVO> getuserSites(String user) {

        List<UserSite> usersites = userSiteRepository.findByUserAndFcyNumberOrderByFcynamAsc(user,2);
        List<SiteVO> list = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (UserSite usrsite : usersites) {
            try {
                if (!StringUtils.isEmpty(usrsite.getFcy().trim()) && !ids.contains(usrsite.getFcy())) {
                    SiteVO siteVO = new SiteVO();
                    String volunits = "",curr = "",massunits = "",distunit="";
                    siteVO.id = usrsite.getFcy();
                    siteVO.value = usrsite.getFcynam();
                    siteVO.maxStops = usrsite.getMaxStops();
                    curr  = usrsite.getCur();
                    if(curr == null) {
                        curr = "EUR";
                    }
                    volunits = usrsite.getVolunit();
                    if(volunits == null){
                        volunits = "L";
                    }
                    distunit =  usrsite.getDistunit();
                    if(distunit == null){
                        distunit = "Kms";
                    }
                     massunits = usrsite.getMassunit();
                    if(massunits == null) {
                        massunits = "LB";
                    }

                    siteVO.cur = curr;
                    siteVO.defflg = usrsite.getDefflg();
                    siteVO.distunit = distunit;
                    siteVO.volunit = volunits;
                    siteVO.massunit = massunits;
                    if (!StringUtils.isEmpty(usrsite.getXx10c_geox()) && !StringUtils.isEmpty(usrsite.getXx10c_geoy())) {
                        try {
                            siteVO.lat = Double.parseDouble(usrsite.getXx10c_geoy());
                            siteVO.lng = Double.parseDouble(usrsite.getXx10c_geox());
                        } catch (Exception e) {
                        }
                    }
                    list.add(siteVO);
                }
                ids.add(usrsite.getFcy());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;

    }


    public SiteVO convertSite(Facility facility) {
        SiteVO siteVO = new SiteVO();
        siteVO.id = facility.getFcy();
        siteVO.value = facility.getFcynam();
        return siteVO;
    }

    public Trip getArrivalSiteforVehice(String veh, Date date){
        String departureSite = null;
        Trip trip = new Trip();
        trip = tripRepository.findLatestDepartSites(veh,date);
        return trip;
    }


    public List<DropsVO> listDrops(String site) {
        log.info("Transport service is loaded...");

        List<Drops> drops = null;

        if (!StringUtils.isEmpty(site)) {
            drops = dropsRepository.findBySite(site);
            if (!CollectionUtils.isEmpty(drops)) {
                return drops.parallelStream().map(a -> this.convertDrops(a))
                        .collect(Collectors.toList());
            }
        } else {
            List<DropsVO> dropsList = new ArrayList<>();
            Iterator<Drops> iterator = dropsRepository.findAll().iterator();
            while (iterator.hasNext()) {
                dropsList.add(this.convertDrops(iterator.next()));
            }
            return dropsList;
        }
        return new ArrayList<>();
    }


    // Added to get VR details from the X3 tables --- by Ashok
    public VehRoute VehRouteByID(String vrcode) {

        //  return  vehRouteRepository.findByYnumpc(vrcode);
        VehRoute vr = vehRouteRepository.findByXnumpc(vrcode);
        if (vr == null) {
            VehRoute vr1 = new VehRoute();
            return vr1;
        }
        return vr;

    }


    public LoadVehStock LoadVehstockByVR(String vrcode) {

        LoadVehStock ls = loadVehStockRepository.findByXvrsel(vrcode);
        if (ls == null) {
            LoadVehStock l = new LoadVehStock();
            return l;
        }
        return ls;
    }


    public List<VehRouteDetail> listVehRouteDetails(String vrnum) {
        log.info("Transport service is loaded...");
        List<VehRouteDetail> listVrd = null;
        listVrd = vehRouteDetailRepository.findByXnumpc(vrnum);
        if (!CollectionUtils.isEmpty(listVrd)) {
            return listVrd;
        }
        return new ArrayList<>();
    }


    private DropsVO convertDrops(Drops drops) {
        DropsVO dropsVO = new DropsVO();
        BeanUtils.copyProperties(drops, dropsVO);
        return dropsVO;
    }

    public List<PickUPVO> listPickups(String site) {
        log.info("Transport service is loaded...");

        List<PickUP> pickUPS = null;

        if (!StringUtils.isEmpty(site)) {
            pickUPS = pickupRepository.findBySite(site);
            if (!CollectionUtils.isEmpty(pickUPS)) {
                return pickUPS.parallelStream().map(a -> this.convertPickups(a))
                        .collect(Collectors.toList());
            }
        } else {
            List<PickUPVO> pickupList = new ArrayList<>();
            Iterator<PickUP> iterator = pickupRepository.findAll().iterator();
            while (iterator.hasNext()) {
                pickupList.add(this.convertPickups(iterator.next()));
            }
            return pickupList;
        }
        return new ArrayList<>();
    }

    private PickUPVO convertPickups(PickUP pickUP) {
        PickUPVO pickUPVO = new PickUPVO();
        BeanUtils.copyProperties(pickUP, pickUPVO);
        return pickUPVO;
    }

    public void deleteTrips(List<TripVO> tripVOList) {
        try {
            log.info("INSIDE deleteTrip");
            for(TripVO tripVO: tripVOList) {
                this.deletesingleTrip(tripVO.getItemCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void lockTrips(List<TripVO> tripVOList) {
        for(TripVO tripvo : tripVOList) {
            List<TripVO> tempVO = new ArrayList<>();
            tempVO.add(tripvo);
            lockTrip(tempVO);
        }
    }

    public void lockTrip(List<TripVO> tripVOList) {
        try {
            log.info("INSIDE locktrip");
            Date currentDate = new Date();
            String date = format.format(currentDate);
            TripVO tripVO = tripVOList.get(0);
            this.deleteTrip(tripVO.getItemCode());
            String docDate = format.format(tripVO.getDate());
            String rtnDate = format.format(tripVO.getEndDate());
			String endDate = format.format(tripVO.getEndDate());
            String execDate = format.format(tripVO.getDatexec());
            String vr = tripVO.getItemCode().toString();

            int tripno = tripVO.getTrips();

            List<Map<String, Object>> list = (List<Map<String, Object>>) tripVO.getTrialerObject();
            String trailer = "",trailer1 = "" ;
            if (!CollectionUtils.isEmpty(list)) {
                log.info("after locking", list);
                Map<String, Object> map = list.get(0);
                log.info("after locking", map);
                if (Objects.nonNull(map.get("trailer"))) {
                    trailer = (String) map.get("trailer");
                }
                if(list.size() > 1) {
                    Map<String, Object> map1 = list.get(1);
                    log.info("after locking", map1);
                    if (Objects.nonNull(map1.get("trailer"))) {
                        trailer1 = (String) map1.get("trailer");
                    }
                }
            }
            Map<String, Object> Vehlist = (Map<String, Object>) tripVO.getVehicleObject();
            String BPTNUM = "";
            BPTNUM = (String) Vehlist.get("bptnum");
            String Veh_code = (String) Vehlist.get("codeyve");
            String driverid = (String) tripVO.getDriverId();
            int yoperation = 1;
            if("Yes".equalsIgnoreCase(Vehlist.get("lateral").toString())) {
                yoperation = 2;
            }
            int yloadbay = 1;
            if("Yes".equalsIgnoreCase(Vehlist.get("loadBay").toString())) {
                yloadbay = 2;
            }
            int ytailgate = 1;
            if("Yes".equalsIgnoreCase(Vehlist.get("tailGate").toString())) {
                ytailgate = 2;
            }
            String query = "INSERT INTO " + dbSchema + ".XX10CPLANCHA\n" +
                    "(UPDTICK_0, " +
                    "XSDHPCKSTA_0, " +
                    "XNUMPC_0, " +
                    "BPTNUM_0, " +
                    "CODEYVE_0, " +
                    "XCODEYVE_0, " +
                    "HEUDEP_0, " +
                    "CREDAT_0, " +
                    "CREUSR_0, " +
                    "UPDUSR_0, " +
                    "UPDDAT_0, " +
                    "OPTIMSTA_0, " +
                    "FCY_0, " +
                    "XVRY_0, " +
                    "JOBID_0, " +
                    "TOTDISTANCE_0, " +
                    "TOTTIME_0, " +
                    "XNUMTV_0, " +
                    "DATLIV_0, " +
                    "HEUARR_0, " +
                    "CREDATTIM_0, " +
                    "UPDDATTIM_0, " +
                    "AUUID_0, " +
                    "DATARR_0, " +
                    "INSTFDR_0, " +
                    "INSTFCU_0, " +
                    "JOBSTATUS_0, " +
                    "HEUEXEC_0, " +
                    "DATEXEC_0, " +
                    "DISPSTAT_0, " +
                    "XVALID_0, " +
                    "DRIVERID_0, " +
                    "XROUTNBR_0, " +
                    "LASTUPDDAT_0, " +
                    "LASTUPDTIM_0, " +
                    "LASTUPDAUS_0, " +
                    "PICKSTRT_0, " +
                    "CHECKIN_0, " +
                    "LOADINGSTR_0, " +
                    "LOADINGEND_0, " +
                    "CHECKOUT_0, " +
                    "RETURNED_0, " +
                    "ADATLIV_0, " +
                    "AHEUDEP_0, " +
                    "ADATARR_0, " +
                    "AHEUARR_0, " +
                    "LOADBAY_0, " +
                    "MASPRO_0, " +
                    "XFLG_0, " +
                    "XSTKVCR_0, " +
                    "XHELPER_0, " +
                    "XSLMAN_0, " +
                    "XTECHN_0, " +
                    "XUSER_0, " +
                    "XSTATUS_0, " +
                    "XSMSCOUNT_0, " +
                    "XDIFTIME_0, " +
                    "XSMSSENT_0, " +
                    "XSEALNUMH_0, " +
                    "XCIGEOX_0, " +
                    "XCIGEOY_0, " +
                    "XCOGEOX_0, " +
                    "XCOGEOY_0, " +
                    "XUNIT_0, " +
                    "XUNIT1_0, " +
                    "XUNIT2_0, " +
                    "XVOLUME_0, " +
                    "XVOL1_0, " +
                    "XVOL2_0, " +
                    "XVOLU_0, " +
                    "XMASSU_0, " +
                    "XMASSU1_0, " +
                    "XVOLU1_0, " +
                    "POURLOAKG_0, " +
                    "POURLOAM3_0, " +
                    "XLINKID_0, " +
                    "XDPRTFDR_0, " +
                    "XRTNFDR_0, " +
                    "RHEUDEP_0, " +
                    "RDATLIV_0, " +
                    "RHEUARR_0, " +
                    "RDATARR_0, " +
                    "TRAILER_0, " +
                    "TRAILER_1, XEQUIPID_0, XEQUIPID_1, XEQUIPID_2, XEQUIPID_3, XEQUIPID_4, XEQUIPID_5, XEQUIPID_6, XEQUIPID_7, XEQUIPID_8, XEQUIPID_9, XEQUIPID_10, XEQUIPID_11, XEQUIPID_12, XEQUIPID_13, XEQUIPID_14, XEQUIPID_15, XEQUIPID_16, XEQUIPID_17, XEQUIPID_18, XEQUIPID_19, XEQUIPID_20, XEQUIPID_21, XEQUIPID_22, XEQUIPID_23, XEQUIPID_24, XEQUIPID_25, XEQUIPID_26, XEQUIPID_27, XEQUIPID_28, XEQUIPID_29, XEQUIPID_30, XEQUIPID_31, XEQUIPID_32, XEQUIPID_33, XEQUIPID_34, XEQUIPID_35, XEQUIPID_36, XEQUIPID_37, XEQUIPID_38, XEQUIPID_39, XEQUIPID_40, XEQUIPID_41, XEQUIPID_42, XEQUIPID_43, XEQUIPID_44, XEQUIPID_45, XEQUIPID_46, XEQUIPID_47, XEQUIPID_48, XEQUIPID_49, XEQUIPID_50, XEQUIPID_51, XEQUIPID_52, XEQUIPID_53, XEQUIPID_54, XEQUIPID_55, XEQUIPID_56, XEQUIPID_57, XEQUIPID_58, XEQUIPID_59, XEQUIPID_60, XEQUIPID_61, XEQUIPID_62, XEQUIPID_63, XEQUIPID_64, XEQUIPID_65, XEQUIPID_66, XEQUIPID_67, XEQUIPID_68, XEQUIPID_69, XEQUIPID_70, XEQUIPID_71, XEQUIPID_72, XEQUIPID_73, XEQUIPID_74, XEQUIPID_75, XEQUIPID_76, XEQUIPID_77, XEQUIPID_78, XEQUIPID_79, XEQUIPID_80, XEQUIPID_81, XEQUIPID_82, XEQUIPID_83, XEQUIPID_84, XEQUIPID_85, XEQUIPID_86, XEQUIPID_87, XEQUIPID_88, XEQUIPID_89, XEQUIPID_90, XEQUIPID_91, XEQUIPID_92, XEQUIPID_93, XEQUIPID_94, XEQUIPID_95, XEQUIPID_96, XEQUIPID_97, XEQUIPID_98, XOPERATION_0, XLOADBAY_0, XXSTATUS_0, XTAILGATE_0, XSOURCE_0, NOTE_0, XACTDISTCKIN_0, XACTDISTCKOT_0,XOLDCODEYVE_0,DISTANCECOST_0,ORDERCOUNT_0,OVERTIMECOST_0,REGULARTIMEC_0,TOTALCOST_0,TOTALDISTANC_0,TOTALTIME_0,TOTALTRAVELT_0,TOTALBREAKSE_0,RENEWALCOUNT_0,TOTALRENEWAL_0,XDESFCY_0, XTREPORT_0, XALLOCFLG_0, XFLOCTYP_0 ,XTLOCTYP_0, XFLOC_0, XTLOC_0, XSDEPOT_0, XDDEPOT_0 )\n" +
                    "VALUES(1,1, '" + tripVO.getItemCode() + "','"+ BPTNUM +"', '" + tripVO.getCode() + "', '','" + tripVO.getStartTime() + "' , '" + date + "', '', '', '" + date + "'" +
                    ", 1, '" + tripVO.getDepSite() + "', 0, '', '" + tripVO.getTotalDistance() + "','" + tripVO.getTotalTime() + "' , '', '" + docDate + "', '" + tripVO.getEndTime() + "', '" + date + "'," +
                    " '" + date + "', 0, '" + endDate + "', '', '', 'Succeeded','" + tripVO.getHeuexec() +"' ,'" + execDate +"' , 1, 0," +
                    " '" + tripVO.getDriverId() + "', '" + tripVO.getTrips() + "', '', '', '', '', '', '', '', ''," +
                    " '', '', '', '', '', 0, 0, 0, '', ''," +
                    " '', '', '', 0, 0, 0, 0, '', '', ''," +
                    " '', '', '', '', '', '', '', '', '', ''," +
                    " '', '', 0, 0, '', 0, 0, '', '', ''," +
                    " '', '" + trailer + "', '" + trailer1 + "', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', '', "+ yoperation +","+ yloadbay +",'',"+ ytailgate +", 2, '" + tripVO.getNotes() + "',0,0,'','" + tripVO.getDistanceCost() + "'," + tripVO.getStops() + ",'" + tripVO.getOvertimeCost() + "','" + tripVO.getRegularCost() + "','" + tripVO.getTotalCost() + "','" + tripVO.getTotalDistance() + "','" + tripVO.getTotalTime() + "' ,'" + tripVO.getTravelTime() + "',0,0,0,'" + tripVO.getArrSite() + "', 0,0, '', '','','', '" + tripVO.getArrivalDepot() + "', '" + tripVO.getDepartureDepot() + "')";

            entityManager.createNativeQuery(query).executeUpdate();

            int sequenceNUm = 2;
            if (null != tripVO.getTotalObject()) {
                String totalObj = mapper.writeValueAsString(tripVO.getTotalObject());
                Map<String, Object> tripObj = mapper.readValue(totalObj, new TypeReference<Map<String, Object>>() {
                });
                String ttObj = mapper.writeValueAsString(tripObj.get("selectedTripData"));
                List<Map<String, Object>> totObj = mapper.readValue(ttObj, new TypeReference<List<Map<String, Object>>>() {
                });
                for (Map<String, Object> map : totObj) {
                    if (map.size() <= 0) {
                        continue;
                    }
                    String docType = null != map.get("doctype") ? map.get("doctype").toString() : "";
                    int docNum = this.getDocType(docType);
                    String Arrtime = null != map.get("arrival") ? map.get("arrival").toString() : "";
                    String StartDate = null != map.get("startDate") ? format.format(format.parse(map.get("startDate").toString())) : "";
                    String docendDate = null != map.get("endDate") ? format.format(format.parse(map.get("endDate").toString())) : "";
               	    String customer = null != map.get("bpcode") ?  map.get("bpcode").toString() : "";
                    String Deptime = null != map.get("end") ? map.get("end").toString() : "";
                    String individualDistance = null != map.get("individualDistance") ? map.get("individualDistance").toString() : "";
                    String SevTime = null != map.get("serTime") ? map.get("serTime").toString() : "";
                    String Traveltime = null != map.get("time") ? map.get("time").toString() : "";
                    String TravelDist = null != map.get("distance") ? map.get("distance").toString() : "";
                    String headertext = null != map.get("noteMessage") ? map.get("noteMessage").toString() : "";
                    String carriertext = null != map.get("CarrierMessage") ? map.get("CarrierMessage").toString() : "";
                    String loadertext = null != map.get("loaderMessage") ? map.get("loaderMessage").toString() : "";
                   // String comments = null != map.get("") ? map.get().toString("") : "";
                    String comments = null !=map.get("noteMessage") ? map.get("noteMessage").toString() : "";
                    if (null != map.get("panelType") && "pickup".equalsIgnoreCase(map.get("panelType").toString())) {
                        this.insertTrip(tripVO.getItemCode(), map, date, sequenceNUm, 1, docNum, StartDate, docendDate);
                    } else {
                        this.insertTrip(tripVO.getItemCode(), map, date, sequenceNUm, 2, docNum, StartDate, docendDate);
                        this.InsertintoSalesOrder(map, docNum , date, Veh_code,driverid,tripVO.getItemCode(), sequenceNUm);
                    }
                    String dDate = (String) map.get("docdate");
                    Date ddDate = format.parse(dDate);
                    Date selectedDate = format.parse(docDate);
                    Date enddate = format.parse(rtnDate);
                    this.updateDocs(vr,Veh_code,docDate,Arrtime,BPTNUM,Deptime,docNum,map.get("docnum").toString(),driverid,tripno,trailer,comments,headertext,carriertext,loadertext,customer, sequenceNUm, StartDate, docendDate,individualDistance);


                    sequenceNUm++;
                }
            }
            Trip actualTrip = tripRepository.findByTripCode(tripVO.getItemCode());
            if (null != actualTrip) {
                actualTrip.setLock(1);
                tripRepository.save(actualTrip);
            }


                // lvs print - data insert into table
                this.insertDataintoPrint(tripVO, docDate, trailer, BPTNUM, date );



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String convertToString(Object value) {
        if(Objects.nonNull(value)) return value.toString();
        return null;
    }


    private void deleteSOrderData(String itemCode) {
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_SORDERH_QUERY, dbSchema, "X10CSOH", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_SORDERH_QUERY, dbSchema, "X10CSOH", itemCode)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }




  // insert data into Sales order grid
  
   private void InsertintoSalesOrder(Map<String, Object> map, int docType , String date , String Vehicle, String Driver, String RouteNo, int seq) {

            if(docType == 4) {
                 log.info("pick ticket sales order insert");
                String pcktno =  this.convertToString(map.get("docnum"));
                List<String> details = docDsRepository.getOrderNoByDocnum(pcktno);

            if(details.size() > 0) {

                for (int i = 0; i < details.size(); i++) {
                    String detail = details.get(i);
                if (Objects.nonNull(detail)) {

                    //check same route no , same SO with empty pick ticket exist
                    SorderList sorder = sorderListRepository.getOpenDocsbyRouteCodeAndSO(RouteNo, detail);
                    if (Objects.nonNull(sorder)) {


                        String queryStr = null;

                        queryStr = MessageFormat.format(UPDATE_SINGLE_QUERY_2_CONDITIONS, dbSchema, "X10CSOH", "XTPTHNUM_0", map.get("docnum"), "XTVRNUM_0", RouteNo, "XTSOHNUM_0", detail);
                        entityManager.createNativeQuery(queryStr).executeUpdate();

                    } else {


                        SorderList orderDetails = sorderListRepository.getLatestLineNobyOrderNo(detail);

                        if (Objects.nonNull(orderDetails)) {

                            log.info(Integer.toString(orderDetails.getLineno()));
                            Integer latestNo = orderDetails.getLineno() + 1000;
                            String sql = "INSERT INTO " + dbSchema + ".X10CSOH\n" +
                                    "(UPDTICK_0, XTLINENUM_0, XTSOHNUM_0, XTLVSNUM_0, XTSDHNUM_0,  CREUSR_0, UPDUSR_0,  XTPTHNUM_0, XTVEH_0, XTVRNUM_0, XTDRI_0, CREDATTIM_0, UPDDATTIM_0, AUUID_0, XSEQ_0)\n" +
                                    "VALUES(1, " + latestNo + ",'" + detail + "','', '', 'RO', 'RO', '" + map.get("docnum") + "', '" + Vehicle + "', '" + RouteNo + "','" + Driver + "', '" + date + "', '" + date + "', 0, " + seq + ")";
                            entityManager.createNativeQuery(sql).executeUpdate();
                        } else {
                            log.info("fresh record");
                            Integer latestNo = 1000;
                            String sql = "INSERT INTO " + dbSchema + ".X10CSOH\n" +
                                    "(UPDTICK_0, XTLINENUM_0, XTSOHNUM_0, XTLVSNUM_0, XTSDHNUM_0,  CREUSR_0, UPDUSR_0, XTPTHNUM_0, XTVEH_0, XTVRNUM_0, XTDRI_0, CREDATTIM_0, UPDDATTIM_0, AUUID_0, XSEQ_0)\n" +
                                    "VALUES(1, " + latestNo + ",'" + detail + "','', '', 'RO', 'RO',  '" + map.get("docnum") + "', '" + Vehicle + "', '" + RouteNo + "','" + Driver + "', '" + date + "', '" + date + "', 0, " + seq + ")";
                            entityManager.createNativeQuery(sql).executeUpdate();

                        }
                    }
                }
            }
            }
            }
            else {
                log.info("other documents sales order insert");

            }

        }


  
  
      

    public void insertDataintoPrint(TripVO tripVO, String docDate,String trailer, String BPTNUM, String date) {

         log.info("inside print");
        String query = "INSERT INTO " + dbSchema + ".XREPORTH\n" +
                "(UPDTICK_0, " +
                "STOFCY_0, " +
                "VCRNUM_0, " +
                "XLOADFLG_0, " +
                "XVRDATE_0, " +
                "TRAILER_0, " +
                "XVRSEL_0, " +
                "XCODEYVE_0, " +
                "XBPTNUM_0, " +
                "DRIVERID_0, " +
                "XPRINT_0, " +
                "XPRINT_1, " +
                "XPRINT_2, " +
                "XPRINT_3, " +
                "XPRINT_4, " +
                "XPRINT_5, " +
                "XPRINT_6, " +
                "XPRINT_7, " +
                "XPRINT_8, " +
                "XPRINT_9, " +
                "XREPNAME_0, " +
                "XREPNAME_1, " +
                "XREPNAME_2, " +
                "XREPNAME_3, " +
                "XREPNAME_4, " +
                "XREPNAME_5, " +
                "XREPNAME_6, " +
                "XREPNAME_7, " +
                "XREPNAME_8, " +
                "XREPNAME_9, " +
                "CREDATTIM_0, " +
                "UPDDATTIM_0, " +
                "AUUID_0, " +
                "CREUSR_0, " +
                "UPDUSR_0 )\n" +
        "VALUES(1,'" + tripVO.getDepSite() + "','" + tripVO.getItemCode() + "',1,'" + docDate + "','" + trailer + "','','" + tripVO.getCode() + "','" + BPTNUM + "','" + tripVO.getDriverId() + "','','','','','','','','','','','ZARCCLIENTRASMQ','ZARCCLIENTRASMP','ZARCCLIENTSDFR1','','','','','','','','" + date + "','" + date + "', 0, '', '')";

        entityManager.createNativeQuery(query).executeUpdate();

    }


	
	

    public void updateValidateVr(String tableName, String field1,String field2,String vrcode,TripVO tripvo){
        String queryStr = null;
        log.info("inside update validate");
        // String date = format.format(currentDate);
        queryStr =  MessageFormat.format(UPDATE_SINGLE_QUERY_INT, dbSchema, tableName, field1,field2,2,vrcode);
        entityManager.createNativeQuery(queryStr).executeUpdate();

          this.createLoadVehStock(vrcode, tripvo);
    }



    // WTA

    public List<Vehicle> listAllTransports() {
        List<Vehicle> vehicleList = vehicleRepository.findAll();
        if (vehicleList == null) {
            vehicleList = new ArrayList<>();
        }
        return vehicleList;
    }

    public List<Driver> listAllOperators() {
        List<Driver> operatorList = driverRepository.findAll();
        if (operatorList == null) {
            operatorList = new ArrayList<>();
        }
        return operatorList;
    }

    //WTA ends



    public void createLoadVehStock(String vr, TripVO tripVO) {
       try {
           VehRoute vehroute = vehRouteRepository.findByXnumpc(vr);
           String LVS,XBPTNUM;
           int onlyReceipts = 0;
           int onlyPickTicket = 2;
           String Appuser = "";
           Date currentDate = new Date();
           String DATENOW = format.format(currentDate);
           log.info("insdie craeteLoadVehStcok");
           log.info(vehroute.getCodeyve());
           Date date1 = format.parse(vehroute.getDatliv());
           XBPTNUM   = vehroute.getBptnum();
           log.info("date =", date1);
           Calendar calendar = Calendar.getInstance();
           calendar.setTime(date1);
           int year = calendar.get(Calendar.YEAR);
           int mon = calendar.get(Calendar.MONTH);
           LVS = this.generateLVScode(vehroute.getFcy(), mon + 1, year);
           log.info(LVS);
           int forceseq = 0;
           if(tripVO.isForceSeq() == true){
               forceseq = 2;
           }else{
               forceseq = 1;
           }

           if (null != tripVO.getTotalObject()) {
               String totalObj3 = mapper.writeValueAsString(tripVO.getTotalObject());
               Map<String, Object> tripObj3 = mapper.readValue(totalObj3, new TypeReference<Map<String, Object>>() {
               });
               String ttObj = mapper.writeValueAsString(tripObj3.get("selectedTripData"));
               List<Map<String, Object>> totObj3 = mapper.readValue(ttObj, new TypeReference<List<Map<String, Object>>>() {
               });
               for (Map<String, Object> map3 : totObj3) {
                   String docType3 = null != map3.get("doctype") ? map3.get("doctype").toString() : "";
                   String pairedDoc = null != map3.get("pairedDoc") ? map3.get("pairedDoc").toString() : "";
                   int docNum3 = this.getDocType(docType3);

                   boolean condition1 = (docNum3 == 4 && pairedDoc.trim().length() < 1);
                   boolean condition2 = (docNum3 == 1 && pairedDoc.trim().length() < 1);
                   boolean condition3 = (onlyPickTicket == 2);


                   if((condition1 && condition3) || (condition2 && condition3) )
                   {
                      onlyPickTicket = 1;
                   }
               }
           }

           if(tripVO.getDrops() == 0 && tripVO.getPickups() > 0){
               onlyReceipts = 2;
               Appuser = vehroute.getDriverid();
           }else {
               onlyReceipts = 1;
               Appuser = vehroute.getDriverid();
           }

           String date = format.format(date1);
           String query = "INSERT INTO " + dbSchema + ".XX10CLODSTOH\n" +
                   "(UPDTICK_0, " +
                   "VCRNUM_0, " +
                   "BETFCYCOD_0, " +
                   "STOFCY_0," +
                   "XDESFCY_0,"+
                   "PURFCY_0, " +
                   "SALFCY_0, " +
                   "FCYADD_0, " +
                   "BPSNUM_0, " +
                   "BPSADD_0, " +
                   "SCOLOC_0, " +
                   "PJT_0, " +
                   "BPCNUM_0, " +
                   "CUR_0, " +
                   "BETCPY_0, " +
                   "INVSGH_0, " +
                   "INVFLG_0, " +
                   "SIHNUM_0, " +
                   "FCYDES_0, " +
                   "IPTDAT_0, " +
                   "VCRDES_0, " +
                   "TRSFAM_0, " +
                   "TRSTYP_0, " +
                   "TRSCOD_0, " +
                   "ENTCOD_0, " +
                   "WRHE_0, " +
                   "EXPNUM_0, " +
                   "IMPNUMLIG_0, " +
                   "CREDAT_0, " +
                   "CREUSR_0, " +
                   "UPDDAT_0, " +
                   "UPDUSR_0, " +
                   "CREDATTIM_0, " +
                   "UPDDATTIM_0, " +
                   "AUUID_0, " +
                   "CFMFLG_0, " +
                   "SGHTYP_0, " +
                   "TMPSGHNUM_0, " +
                   "MANDOC_0, " +
                   "ATDTCOD_0, " +
                   "DPEDAT_0, " +
                   "ETD_0, " +
                   "ARVDAT_0, " +
                   "ETA_0, " +
                   "LICPLATE_0, " +
                   "TRLLICPLATE_0, " +
                   "DRIVERID_0, " +
                   "XSALEMEN_0, " +
                   "XOPERATOR_0, " +
                   "XTECHN_0, " +
                   "XAPPUSR_0, " +
                   "XVCRNUM_0, " +
                   "XRETURNFLG_0, " +
                   "XACTFLG_0, " +
                   "XBUSTYP1_0, " +
                   "XBUSTYP2_0, " +
                   "XVRSEL_0, " +
                   "XLOADREF_0, " +
                   "CODEYVE_0, " +
                   "XVALFLG_0, " +
                   "LOCSEL_0, " +
                   "XROUTNBR_0, " +
                   "XTEXTNUM_0, " +
                   "XTOTNONSTK_0, " +
                   "XCODEYVE_0, " +
                   "XLOADFLG_0, " +
                   "X10CHKIN_0, " +
                   "XXIPTDAT_0, " +
                   "XSCHREALC_0, " +
                   "XCAPACITIES_0, " +
                   "XVEHVOL_0, " +
                   "XTOTSHESTK_0, " +
                   "XSEALNUMH_0, " +
                   "XUNLOADFLG_0, " +
                   "XSTARTODMTR_0, " +
                   "XENDODMTR_0, " +
                   "XCHKINDAT_0, " +
                   "XCHKINTIM_0, " +
                   "XCHKOUDAT_0, " +
                   "XCHKOUTIM_0, " +
                   "XPMASS_0, " +
                   "XPVOL_0," +
                   "XMASS_0," +
                   "XVMASS_0, " +
                   "XLMASS_0, " +
                   "XVOLCAM_0," +
                   "XVEHV_0," +
                   "XMPVOL_0," +
                   "XUNLOADDATE_0," +
                   "XUNLOADEDBY_0," +
                   "XUNLOADTIME_0," +
                   "TRAILER_0," +
                   "TRAILER_1," +
                   "TRAILER2_0," +
                   "XVRDATE_0," +
                   "XSOURCELOC_0," +
                   "XLOADBAYD_0," +
                   "XLOADBAYR_0," +
                   "XDEVICEID_0," +
                   "XOLDCODEYVE_0," +
                   "XNOTE1_0," +
                   "DIE_0, DIE_1, DIE_2, DIE_3, DIE_4, DIE_5, DIE_6, DIE_7, DIE_8, DIE_9, DIE_10, DIE_11, DIE_12, DIE_13, DIE_14, DIE_15, DIE_16, DIE_17, DIE_18, DIE_19,CCE_0,CCE_1, CCE_2, CCE_3, CCE_4, CCE_5, CCE_6, CCE_7,CCE_8,CCE_9, CCE_10, CCE_11, CCE_12, CCE_13, CCE_14, CCE_15, CCE_16, CCE_17, CCE_18, CCE_19, XBUSTYP3_0, XBPTNUM_0,XECODEYVE_0,XEDRIVERID_0,XEREGSTR_0,XETRAILER_0,XETREGSTR_0,XLOG_0,XPDLOG_0,XSTOVAL_0,XMOB_0, XWEB_0,XFORSEQ_0, XLOADUSR_0, XLOADNAM_0, XLOADEML_0, XDRN_0,XPODSUB_0, XPODSTATUS_0, XLODAPPSTA_0 ,XROUTSTAT_0, XTRIP_0, MDL_0,XMLDUSER_0,XPOHNUM_0,XSTATUS_0, XALLFLG_0,XSDEPOT_0, XDDEPOT_0)\n" +
                   "VALUES(1, '" + LVS + "',0, '" + vehroute.getFcy() + "','" + vehroute.getXdesfcy() + "', '','' , '', '', '', '', '','','','',0,0,0,'','" + date + "','','',0," +
                   " '','','',0,0,'" + DATENOW + "','','" + DATENOW + "','','" + DATENOW + "','" + DATENOW + "',0x41027895E7BDB649ADD9317F23ADF47A,0,'','','','','" + date + "','" + vehroute.getHeudep() + "','" + vehroute.getDatarr() + "','" + vehroute.getHeuarr() + "','" + vehroute.getCodeyve() + "','','" + vehroute.getDriverid() + "','','',''," +
                   " '"+Appuser+"','" + LVS + "',0,1,2,1,'" + vr + "','','',"+onlyPickTicket+",''," + vehroute.getXroutnbr() + ",'',0.00,'" + vehroute.getCodeyve() + "',2,1,'" + date + "',2,0.0,0.00,0.00,'',0,0,0,'','','','','','','','','','','','','','','','" + vehroute.getTrailer() + "','" + vehroute.getTrailer_1() + "','', " +
                   " '" + date + "','',0,0,'','','" + vehroute.getNote() + "','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','',1,'"+XBPTNUM+"','','','','','','','',1,'','',"+forceseq+",'','','', '',1,1,1,1,1,'','','','',0,'" + tripVO.getArrivalDepot() + "', '" + tripVO.getDepartureDepot() + "')";
           entityManager.createNativeQuery(query).executeUpdate();


           int seqNUm = 2;
           if (null != tripVO.getEquipmentObject()) {

               String ttObj = mapper.writeValueAsString(tripVO.getEquipmentObject());
               List<Map<String, Object>> totObj = mapper.readValue(ttObj, new TypeReference<List<Map<String, Object>>>() {
               });
               for (Map<String, Object> map : totObj) {
                   if (map.size() <= 0) {
                       continue;
                   }
                   this.insertEquipment(LVS, DATENOW, seqNUm, map);
                   seqNUm++;
               }
           }


           //to update the LVS number in the SO for Picktickets
           int sequenceNUm = 2;
           if (null != tripVO.getTotalObject()) {
               String totalObj = mapper.writeValueAsString(tripVO.getTotalObject());
               Map<String, Object> tripObj = mapper.readValue(totalObj, new TypeReference<Map<String, Object>>() {
               });
               String ttObj = mapper.writeValueAsString(tripObj.get("selectedTripData"));
               List<Map<String, Object>> totObj = mapper.readValue(ttObj, new TypeReference<List<Map<String, Object>>>() {
               });
               for (Map<String, Object> map : totObj) {
                   if (map.size() <= 0) {
                       continue;
                   }
                   String docType = null != map.get("doctype") ? map.get("doctype").toString() : "";
                   int docNum = this.getDocType(docType);
                   String number = map.get("docnum").toString();

                   log.info("DOCument number");
                   log.info(map.get("docnum").toString());
                   if(docNum == 4)
                   {
                       //check SO list based on the Pickticket number
                       this.CheckSOlistfromPickTicket(map.get("docnum").toString(), LVS);
                   }

                   sequenceNUm++;
               }
           }




       } catch (Exception e) {
        e.printStackTrace();
    }
    }

    private String generatePTHeaderLinkDlvy ()
    {
        int xcount = 0;
        String latest_TEXCLOB = "";
        List<Texclob> ls = texclobRepository.findByCodeToDelivery();
        if(ls.size() > 0){
            String code = ls.get(0).getCode();
            log.info(code);
            String latestcode = code.substring(code.length() - 8,code.length());
            log.info(latestcode);
            String strPattern = "^0+(?!$)";
            String str1 = latestcode.replaceAll(strPattern, "");
            log.info(str1);
            xcount = Integer.parseInt(str1);
            String str2 = String.format("%08d", xcount+1);
            log.info(str2);
            latest_TEXCLOB = MessageFormat.format(TEXT_CLOB,"SDH~",str2);
            return latest_TEXCLOB;
        }
      return latest_TEXCLOB;
    }

    private String generatePTHeaderLinkRecept ()
    {
        int xcount = 0;
        String latest_TEXCLOB = "";
        List<Texclob> ls = texclobRepository.findByCodeToReceipt();
        if(ls.size() > 0){
            String code = ls.get(0).getCode();
            log.info(code);
            String latestcode = code.substring(code.length() - 8,code.length());
            log.info(latestcode);
            String strPattern = "^0+(?!$)";
            String str1 = latestcode.replaceAll(strPattern, "");
            log.info(str1);
            xcount = Integer.parseInt(str1);
            String str2 = String.format("%08d", xcount+1);
            log.info(str2);
            latest_TEXCLOB = MessageFormat.format(TEXT_CLOB,"XX10CPTH~",str2);
            return latest_TEXCLOB;
        }
        return latest_TEXCLOB;
    }

    private String generatePTHeaderLinkPickTckt()
    {
        int xcount = 0;
        String latest_TEXCLOB = "";
        List<Texclob> ls = texclobRepository.findByCodeToPick();
        if(ls.size() > 0){
            String code = ls.get(0).getCode();
            log.info(code);
            String latestcode = code.substring(code.length() - 8,code.length());
            log.info(latestcode);
            String strPattern = "^0+(?!$)";
            String str1 = latestcode.replaceAll(strPattern, "");
            log.info(str1);
            xcount = Integer.parseInt(str1);
            String str2 = String.format("%08d", xcount+1);
            log.info(str2);
            latest_TEXCLOB = MessageFormat.format(TEXT_CLOB,"PRH~",str2);
            return latest_TEXCLOB;
        }
        return latest_TEXCLOB;
    }


    private String generatePTHeaderLinkMiscDoc()
    {
        int xcount = 0;
        String latest_TEXCLOB = "";
        List<Texclob> ls = texclobRepository.findByCodeToPick();
        if(ls.size() > 0){
            String code = ls.get(0).getCode();
            log.info(code);
            String latestcode = code.substring(code.length() - 8,code.length());
            log.info(latestcode);
            String strPattern = "^0+(?!$)";
            String str1 = latestcode.replaceAll(strPattern, "");
            log.info(str1);
            xcount = Integer.parseInt(str1);
            String str2 = String.format("%08d", xcount+1);
            log.info(str2);
            latest_TEXCLOB = MessageFormat.format(TEXT_CLOB,"PRH~",str2);
            return latest_TEXCLOB;
        }
        return latest_TEXCLOB;
    }

    private String generatePTHeaderLinkBOL()
    {
        int xcount = 0;
        String latest_TEXCLOB = "";
        List<Texclob> ls = texclobRepository.findByCodeToBol();
        if(ls.size() > 0){
            String code = ls.get(0).getCode();
            log.info(code);
            String latestcode = code.substring(code.length() - 8,code.length());
            log.info(latestcode);
            String strPattern = "^0+(?!$)";
            String str1 = latestcode.replaceAll(strPattern, "");
            log.info(str1);
            xcount = Integer.parseInt(str1);
            String str2 = String.format("%08d", xcount+1);
            log.info(str2);
            latest_TEXCLOB = MessageFormat.format(TEXT_CLOB,"BOL~",str2);
            return latest_TEXCLOB;
        }
        return latest_TEXCLOB;
    }
  private  String  generateLVScode(String site, int m,int y){

      // List<LoadVehStock> loadvehstk = loadVehStockRepository.findBySiteandDate(site, m,y);
       //int count=loadvehstk.size();
      int count  = 0;
      List<LoadVehStock> loadvehstk =  loadVehStockRepository.findBySiteandDateOrderByVcrnumDesc(site, m,y);
      if(loadvehstk.size() > 0){
          String itemCode = loadvehstk.get(0).getVcrnum();
          log.info(itemCode);
          String tripCodeNumber = itemCode.substring(itemCode.length() - 7, itemCode.length());
          String strPattern = "^0+(?!$)";
          String str1 = tripCodeNumber.replaceAll(strPattern, "");
          log.info(str1);
          count = Integer.parseInt(str1);
      }

       String sMonth = "";
      if (m < 10) {
          sMonth = "0"+String.valueOf(m);
      } else {
          sMonth = String.valueOf(m);
      }


      int x = y % 100;
      String str = String.format("%07d", count+1);
      log.info(str);
      String latest_LVS = MessageFormat.format(LVS_NUMBER, site,x,sMonth,"XCHG",str);
       return latest_LVS;
  }


    public Map<String,String> ValidateListofTrips(List<TripVO> tripVOList)throws Exception {
        try {
            for (TripVO tripVO : tripVOList) {

                if (tripVO.isLock() && !tripVO.isTmsValidated()) {

                    String VRcode = tripVO.getItemCode().toString();
                    this.updateValidateVr("XX10CPLANCHA", "XVALID_0", "XNUMPC_0", VRcode, tripVO);
                    //entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHA", VRcode)).executeUpdate();

                }
            }
        }
        catch (Exception e) {
            //   entityManager.createNativeQuery("INSERT INTO DEMOTMS.XTMSTEMP VALUES(100);").executeUpdate();

            e.printStackTrace();
            throw e;
        }

        Map<String, String> map = new HashMap<>();
        map.put("success", "success");
        return map;
    }



    public Map<String, String> ValidateTrips(TripVO tripVO) throws Exception {
            try {
                log.info("inside Validate service");

                //for (TripVO tripVO : tripVOList) {
                    String VRcode = tripVO.getItemCode().toString();
                    this.updateValidateVr("XX10CPLANCHA","XVALID_0","XNUMPC_0",VRcode,tripVO);
                   //entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHA", VRcode)).executeUpdate();

                //}
                Map<String, String> map = new HashMap<>();
                map.put("success", "success");
                return map;
            } catch (Exception e) {
                //   entityManager.createNativeQuery("INSERT INTO DEMOTMS.XTMSTEMP VALUES(100);").executeUpdate();

                e.printStackTrace();
                throw e;
            }
        }

    public Map<String, String> NonValidateTrips(TripVO tripVO) throws Exception {
        try {
            log.info("inside NonValidate service");
            //for (TripVO tripVO : tripVOList) {
            String VRcode = tripVO.getItemCode().toString();
            this.updateNonValidateVr("XX10CPLANCHA","XVALID_0","XNUMPC_0",VRcode,tripVO);
            //entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHA", VRcode)).executeUpdate();
            //}
            Map<String, String> map = new HashMap<>();
            map.put("success", "success");
            return map;
        } catch (Exception e) {
            //   entityManager.createNativeQuery("INSERT INTO DEMOTMS.XTMSTEMP VALUES(100);").executeUpdate();
            e.printStackTrace();
            throw e;
        }
    }

    //  update for NonValidation
    public void updateNonValidateVr(String tableName, String field1,String field2,String vrcode,TripVO tripvo){
        String queryStr = null;
        log.info("inside update validate");
        // String date = format.format(currentDate);
        queryStr =  MessageFormat.format(UPDATE_SINGLE_QUERY_INT, dbSchema, tableName, field1,field2,1,vrcode);
        entityManager.createNativeQuery(queryStr).executeUpdate();
        this.removeLVS(vrcode, tripvo);
    }

    public void removeLVS(String vr, TripVO tripVO) {
        log.info("inside LVSdelete Trip");
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_LVS_QUERY, dbSchema, "XX10CLODSTOH", vr)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_LVS_QUERY, dbSchema, "XX10CLODSTOH", vr)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void deletesingleTrip(String itemCode) {

        log.info("inside delete Trip");
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_SINGLETRIP_QUERY, dbSchema, "XX10TRIPS", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_SINGLETRIP_QUERY, dbSchema, "XX10TRIPS", itemCode)).executeUpdate();
            }
			
			this.updateDocAfterDeleteTrip(itemCode);
            this.deleteSOrderData(itemCode);
			
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


 private void updateDocumentAfterDeleteDocument(String docnum , String type) {
        //sdelivery updation
        try {
            if ("DLV".equalsIgnoreCase(type)) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_doc_DELETION, dbSchema, "SDELIVERY", docnum,"",8,"LICPLATE_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0","SDHNUM_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //pick ticket updation
        try {
            if ("PICK".equalsIgnoreCase(type)) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_doc_DELETION, dbSchema, "STOPREH", docnum,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0","PRHNUM_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
     //CUSTOMER RETURNS updation
     try {
         if ("PICK".equalsIgnoreCase(type)) {
             entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_doc_DELETION, dbSchema, "SRETURN", docnum,"",8,"XX10C_LICPLA_0","ETAR_0","ETDR_0","XX10C_BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDATR_0","DPEDATR_0","XCOMMENT_0","SRHNUM_0")).executeUpdate();
         }
     } catch (Exception e) {
         e.printStackTrace();
     }
        //receipt updation
        try {
            if ("PRECEIPT".equalsIgnoreCase(type)) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_doc_DELETION, dbSchema, "XX10CREC", docnum,"",8,"XX10C_LICPLA_0","XETA_0","XETD_0","XBPTNUM_0","XDRIVERID_0","XTRAILER_0","XROUTNBR_0","XARVDAT_0","XDPEDAT_0","XCOMMENT_0", "XPTHNUM_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


     try {
         if ("MISCPICK".equalsIgnoreCase(type)) {
             entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_doc_DELETION, dbSchema, "XX10CMISSTO", docnum,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0","XMSNUM_0")).executeUpdate();
         }
     } catch (Exception e) {
         e.printStackTrace();
     }

     try {
         if ("MISCDROP".equalsIgnoreCase(type)) {
             entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_doc_DELETION, dbSchema, "XX10CMISSTO", docnum,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0","XMSNUM_0")).executeUpdate();
         }
     } catch (Exception e) {
         e.printStackTrace();
     }

    }
    private void updateDocAfterreDesignTrip(String itemCode) {
        //sdelivery updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "SDELIVERY", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "SDELIVERY", itemCode,"",8,"LICPLATE_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //pick ticket updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "STOPREH", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "STOPREH", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //CUSTOMER RETURNS updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "SRETURN", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "SRETURN", itemCode,"",8,"XX10C_LICPLA_0","ETAR_0","ETDR_0","XX10C_BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDATR_0","DPEDATR_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //receipt updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "XX10CREC", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "XX10CREC", itemCode,"",8,"XX10C_LICPLA_0","XETA_0","XETD_0","XBPTNUM_0","XDRIVERID_0","XTRAILER_0","XROUTNBR_0","XARVDAT_0","XDPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

// MISC
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "XX10CMISSTO", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "XX10CMISSTO", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateDocAfterDeleteTrip(String itemCode) {
        //sdelivery updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "SDELIVERY", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "SDELIVERY", itemCode,"",8,"LICPLATE_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //pick ticket updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "STOPREH", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "STOPREH", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //CUSTOEMR RETURN updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "SRETURN", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "SRETURN", itemCode,"",8,"XX10C_LICPLA_0","ETAR_0","ETDR_0","XX10C_BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDATR_0","DPEDATR_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //receipt updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "XX10CREC", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "XX10CREC", itemCode,"",8,"XX10C_LICPLA_0","XETA_0","XETD_0","XBPTNUM_0","XDRIVERID_0","XTRAILER_0","XROUTNBR_0","XARVDAT_0","XDPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //misc trips updation
        //MISC updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "XX10CMISSTO", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_VR_DELETION, dbSchema, "XX10CMISSTO", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    private void deleteTrip(String itemCode) {

        log.info("inside delete Trip");
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHA", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_TRIP_QUERY, dbSchema, "XX10CPLANCHA", itemCode)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHD", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_TRIP_QUERY, dbSchema, "XX10CPLANCHD", itemCode)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //sdelivery updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "SDELIVERY", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_VR_DELETION, dbSchema, "SDELIVERY", itemCode,"",8,"LICPLATE_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //pick ticket updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "STOPREH", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_VR_DELETION, dbSchema, "STOPREH", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //MISC updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "XX10CMISSTO", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_VR_DELETION, dbSchema, "XX10CMISSTO", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		
		
		        // lvs print table
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_LVS_QUERY_2, dbSchema, "XREPORTH", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_TRIP_QUERY_2, dbSchema, "XREPORTH", itemCode)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


		
		
        //CUSTOMER RETURN updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "SRETURN", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_VR_DELETION, dbSchema, "SRETURN", itemCode,"",8,"XX10C_LICPLA_0","ETAR_0","ETDR_0","XX10C_BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDATR_0","DPEDATR_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //receipt updation
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_DOC_CHECK_QUERY, dbSchema, "XX10CREC", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_VR_DELETION, dbSchema, "XX10CREC", itemCode,"",8,"XX10C_LICPLA_0","XETA_0","XETD_0","XBPTNUM_0","XDRIVERID_0","XTRAILER_0","XROUTNBR_0","XARVDAT_0","XDPEDAT_0","XCOMMENT_0")).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void updateDelivery(Date date, int docNum, String docnum) {
        if (docNum == 1) {
            this.updateRecord("SDELIVERY", "DLVDAT_0", "SHIDAT_0", "SDHNUM_0", date, docnum);
        } else if (docNum == 4) {
            this.updateRecord("STOPREH", "DLVDAT_0", "SHIDAT_0", "PRHNUM_0", date, docnum);
        } else if (docNum == 2) {
            this.updateRecord("XX10CREC", "XRCPDAT_0", "XPTHNUM_0", null, date, docnum);
        }else if (docNum == 3) {
            this.updateRecord("SRETURN", "RTNDAT_0", "SRHNUM_0", null, date, docnum);
        }
        else if (docNum == 6) {
            this.updateRecord("XX10CMISSTO", "DLVDAT_0", "XMSNUM_0", null, date, docnum);
        }
    }
	
	
	
    //for scheduler
    private  void updateDocsAtTripCreation(String vr, String Veh_code, String ddate, int docNum, String docnum, int Tripno,int seqno, String licplate ) {
        log.info("inside docattripcreation");
        if(docNum == 1) {
            this.updatedocumentsforSchduler("SDELIVERY", "XX10C_NUMPC_0", "LICPLATE_0",  "SDHNUM_0", "XDLV_STATUS_0", vr, Veh_code, ddate,  docnum, 1, "DLVDAT_0", "XROUTNBR_0",Tripno,"XSEQUENCE_0",seqno, "SHIDAT_0" , "TRLLICPLATE_0", licplate );
        }
        else if(docNum == 4) {
            this.updatedocumentsforSchduler("STOPREH", "XX10C_NUMPC_0", "XX10C_LICPLA_0", "PRHNUM_0", "XDLV_STATUS_0",  vr, Veh_code, ddate, docnum, 1 , "DLVDAT_0", "XROUTNBR_0",Tripno,"XSEQUENCE_0",seqno, "SHIDAT_0", "TRLLICPLATER_0", licplate );
        }
        else if(docNum == 2) {
            this.updatedocumentsforSchduler("XX10CREC", "XX10C_NUMPC_0", "XX10C_LICPLA_0",  "XPTHNUM_0","XDLV_STATUS_0",vr,Veh_code,ddate,docnum,1 , "XRCPDAT_0", "XROUTNBR_0",Tripno,"XSEQUENCE_0",seqno, "XODLVDAT_0" , "XTRLLICPLATE_0", licplate );
        }
        else if(docNum == 3) {
            this.updatedocumentsforSchduler("SRETURN", "XX10C_NUMPC_0", "XX10C_LICPLA_0",  "SRHNUM_0","XDLV_STATUS_0",vr,Veh_code,ddate,docnum,1 , "RTNDAT_0", "XROUTNBR_0",Tripno,"XSEQUENCE_0",seqno, "DLVDAT_0", "TRLLICPLATER_0", licplate );
        }
        else if(docNum == 6) {
            this.updatedocumentsforSchduler("XX10CMISSTO", "XX10C_NUMPC_0", "XX10C_LICPLA_0",  "XMSNUM_0","XDLV_STATUS_0",vr,Veh_code,ddate,docnum,1 , "DLVDAT_0", "XROUTNBR_0",Tripno,"XSEQUENCE_0",seqno, "XREFDAT_0", "TRLLICPLATE_0", licplate );
        }
        }


    private void updateDocs(String vr,String Veh_code,String ddate,String arvtime,String carrier, String Deptime, int docNum, String docnum,String driverid,int tripnum,String trail,String comments,String PTheader, String CarrierText, String LoaderText, String customer, int seqno,String startDate, String endDate, String individualDistance)
    {
        if(docNum == 1) {
            this.updatedocument("SDELIVERY", "XX10C_NUMPC_0", "LICPLATE_0", "ETA_0", "ETD_0" , "SDHNUM_0","XDLV_STATUS_0","ARVDAT_0", "DPEDAT_0","BPTNUM_0","DRIVERID_0","XTRAILER_0",vr,Veh_code,ddate,arvtime,carrier,Deptime,docnum,1,driverid,"XROUTNBR_0",tripnum,trail,"XCOMMENT_0",comments,PTheader,docNum,"XSEQUENCE_0",seqno,startDate, endDate, "XTOTDISTANCE_0", individualDistance);

            if(CarrierText.trim().length() > 0) {
                this.carrierInstruction(CarrierText, docnum);
            }
            log.info("at loader -1");
            log.info(LoaderText);
            if(LoaderText.trim().length() > 0) {
                log.info("at inside loader - 1");
                this.loaderInstruction(LoaderText, docnum,docNum,ddate,customer);
            }

        }else if(docNum == 4) {
            this.updatedocument("STOPREH", "XX10C_NUMPC_0", "XX10C_LICPLA_0", "ETA_0", "ETD_0",  "PRHNUM_0","XDLV_STATUS_0","ARVDAT_0", "DPEDAT_0","BPTNUM_0","DRIVERID_0","XTRAILER_0",vr,Veh_code,ddate,arvtime,carrier,Deptime,docnum,1,driverid,"XROUTNBR_0",tripnum,trail,"XCOMMENT_0",comments,PTheader,docNum,"XSEQUENCE_0",seqno,startDate, endDate, "XTOTDISTANCE_0", individualDistance);
            log.info("at loader -4");
            log.info(LoaderText);
            if(LoaderText.trim().length() > 0) {
                log.info("at inside loader - 4");
                this.loaderInstruction(LoaderText, docnum,docNum,ddate,customer);
            }

        }else if(docNum == 2) {
            this.updatedocument("XX10CREC", "XX10C_NUMPC_0", "XX10C_LICPLA_0", "XETA_0", "XETD_0", "XPTHNUM_0","XDLV_STATUS_0","XARVDAT_0", "XDPEDAT_0","XBPTNUM_0","XDRIVERID_0","XTRAILER_0",vr,Veh_code,ddate,arvtime,carrier,Deptime,docnum,1,driverid,"XROUTNBR_0",tripnum,trail,"XCOMMENT_0",comments,PTheader,docNum,"XSEQUENCE_0",seqno,startDate, endDate, "XTOTDISTANCE_0", individualDistance);
        }else if(docNum == 3) {
            this.updatedocument("SRETURN", "XX10C_NUMPC_0", "XX10C_LICPLA_0", "ETAR_0", "ETDR_0", "SRHNUM_0","XDLV_STATUS_0","ARVDATR_0", "DPEDATR_0","XX10C_BPTNUM_0","DRIVERID_0","XTRAILER_0",vr,Veh_code,ddate,arvtime,carrier,Deptime,docnum,1,driverid,"XROUTNBR_0",tripnum,trail,"XCOMMENT_0",comments,PTheader,docNum,"XSEQUENCE_0",seqno,startDate, endDate, "XTOTDISTANCE_0", individualDistance);
        }
        else if(docNum == 6) {
            this.updatedocument("XX10CMISSTO", "XX10C_NUMPC_0", "XX10C_LICPLA_0", "ETA_0", "ETD_0", "XMSNUM_0","XDLV_STATUS_0","ARVDAT_0", "DPEDAT_0","BPTNUM_0","DRIVERID_0","XTRAILER_0",vr,Veh_code,ddate,arvtime,carrier,Deptime,docnum,1,driverid,"XROUTNBR_0",tripnum,trail,"XCOMMENT_0",comments,PTheader,docNum,"XSEQUENCE_0",seqno,startDate, endDate, "XTOTDISTANCE_0", individualDistance);
        }
    }

    //loader instructions
    private  void loaderInstruction(String LoaderText,String DOCNUM, int DOCTYP, String DOCDATE, String CUSTINFO) {
        String queryStr, queryStr1 = null;
        String NewTexteCode = "";
        log.info("inside update document - loader instructions");
        if (LoaderText.trim().length() > 0) {

            LoadMsg loadmsg = loadMsgRepository.findByDocnum(DOCNUM);


            if (loadmsg == null) {
                Date currentDate = new Date();
                String DATENOW = format.format(currentDate);
                String query = "INSERT INTO " + dbSchema + ".XX10CLI\n" +
                        "(UPDTICK_0, " +
                        "XSDHNUM_0, " +
                        "XREFERENCE_0," +
                        "XDLVDAT_0," +
                        "XPONUM_0," +
                        "XBPCORD_0," +
                        "CREDATTIM_0, " +
                        "UPDDATTIM_0, " +
                        "CREUSR_0," +
                        "UPDUSR_0," +
                        "XSEALNO_0," +
                        "XSEALNO_1," +
                        "XSEALNO_2," +
                        "XTANKNO_0," +
                        "XTANKNO_1," +
                        "XNAOH_0," +
                        "XNA2O_0," +
                        "XPERCENT_0," +
                        "XSPG_0, " +
                        "XTEMP_0," +
                        "XA_0," +
                        "XB_0," +
                        "XC_0," +
                        "XSPLPERNO_0, " +
                        "XGROWEI_0, " +
                        "XGWEI_0," +
                        "XNETWEI_0," +
                        "XNWEI_0," +
                        "XTARAWEI_0," +
                        "XTWEI_0," +
                        "XFILTIM_0," +
                        "XPH_0," +
                        "XACTGAL_0," +
                        "XGAL15_0," +
                        "XCL2WEI_0," +
                        "XTONS_0," +
                        "XBPCSPEF_0," +
                        "XLDRINST_0," +
                        "XSPLINS_0," +
                        "ZDRYLBS_0," +
                        "ZLBSG_0," +
                        "XVALID_0," +
                        "XDOCTYP_0," +
						"XLODSTATUS_0," +
                        "AUUID_0)\n" +
                        "VALUES(1, '" + DOCNUM + "','','" + DOCDATE + "','','" + CUSTINFO + "','" + DATENOW + "','" + DATENOW + "','RO','RO','','','','','',0.00,0.00,0.00,0.00,0,0.00,0.00,0.00,'',0.00,'',0.00,'',0.00,'','',0.00,0.00,0.00,0.00,0.00,'','" + LoaderText + "','',0.00,0.00,0," + DOCTYP + ",1,0x41027895E7BDB649ADD9317F23ADF47A)";
                entityManager.createNativeQuery(query).executeUpdate();

            }
            else {
                String sdhnum = loadmsg.getDocnum();
                    queryStr1 = MessageFormat.format(UPDATE_PTHEADER_QUERY, dbSchema, "XX10CLI", "XLDRINST_0", "XSDHNUM_0", LoaderText, sdhnum);
                    entityManager.createNativeQuery(queryStr1).executeUpdate();

                }
            }
        }
		
 public List<RouteCode> getRouteCodes() {

       List<RouteCode> RouteCodelist = routeCodeRepository.findAll();
       return RouteCodelist;
   }



    private  void carrierInstruction(String carrierinfo,String docnum) {
        String queryStr, queryStr1 = null;
        String NewTexteCode = "";
        log.info("inside update document - carr instructions");
        if (carrierinfo.trim().length() > 0) {

            DvlBol bol = dlvyBolRepository.findByDocnum(docnum);


            if(bol == null){

                queryStr =  MessageFormat.format(UPDATE_SINGLE_QUERY, dbSchema, "SDELIVERY", "XCARRINST_0", "SDHNUM_0", carrierinfo,docnum);
                entityManager.createNativeQuery(queryStr).executeUpdate();

            }
            else {
                String bolnum = bol.getBolnum();
                if (bol.getBllink().trim().length() > 0) {
                    String ptlinkdata = bol.getBllink();
                    queryStr1 = MessageFormat.format(UPDATE_PTHEADER_QUERY, dbSchema, "TEXCLOB", "TEXTE_0", "CODE_0", carrierinfo, ptlinkdata);
                    entityManager.createNativeQuery(queryStr1).executeUpdate();

                } else {
                        NewTexteCode = generatePTHeaderLinkBOL();

                    Date currentDate = new Date();
                    String DATENOW = format.format(currentDate);

                    String query = "INSERT INTO " + dbSchema + ".TEXCLOB\n" +
                            "(UPDTICK_0, " +
                            "CODE_0, " +
                            "TEXTE_0," +
                            "IDENT1_0," +
                            "IDENT2_0," +
                            "IDENT3_0," +
                            "CREDAT_0, " +
                            "CREUSR_0, " +
                            "CRETIM_0," +
                            "UPDDAT_0, " +
                            "UPDUSR_0, " +
                            "UPDTIM_0," +
                            "CREDATTIM_0, " +
                            "UPDDATTIM_0, " +
                            "AUUID_0)\n" +
                            "VALUES(1, '" + NewTexteCode + "','" + carrierinfo + "','','','','" + DATENOW + "','RO',0,'" + DATENOW + "','RO',0,'" + DATENOW + "','" + DATENOW + "',0x41027895E7BDB649ADD9317F23ADF47A)";
                    entityManager.createNativeQuery(query).executeUpdate();

                    this.insertPTHeaderTexttoBOL(NewTexteCode, carrierinfo, bolnum);
                }

            }
        }
    }

	   //update documents after trip creation
    private void updatedocumentsforSchduler(String tableName, String field1, String field2, String filed3,String field4,String vr,String Veh_code,String ddate,String docnum,int dly_status, String field6,String field7,int tripno,String field8,int seqno, String field9, String field10 , String licplate){
        String queryStr,queryStr1  = null;
        String NewTexteCode = "";
        log.info("inside update document");
        // String date = format.format(currentDate);
        queryStr =  MessageFormat.format(UPDATE_doc_QUERY_AT_TripCreation, dbSchema, tableName, field1, field2, filed3,field4,vr,Veh_code,ddate,docnum,dly_status,field6,field7,tripno,field8,seqno, field9, field10 , licplate);
        entityManager.createNativeQuery(queryStr).executeUpdate();
    }

    //update document
    private void updatedocument(String tableName, String field1, String field2, String filed3, String field4, String field5, String field6,String field7, String field8, String field9,String field10,String field12,String vr,String Veh_code,String ddate,String arvtime,String Carrier,String deptime, String docnum,int dly_status,String DriverID,String field11,int tripno,String trail,String field13,String comments,String PTheader,int type,String field14 , int seq,String sdate , String edate, String indDistField, String indDistValue)
    {

        String queryStr,queryStr1  = null;
        String NewTexteCode = "";
        log.info("inside update document");
        // String date = format.format(currentDate);
        queryStr =  MessageFormat.format(UPDATE_doc_QUERY, dbSchema, tableName, field1, field2, filed3,field4,field5,field6, field7, field8,field9,field10,vr,Veh_code,arvtime,deptime,docnum,dly_status,ddate,Carrier,DriverID,field11,tripno,field12,trail,field13,comments, field14 , seq, sdate, edate, indDistField, indDistValue);
        entityManager.createNativeQuery(queryStr).executeUpdate();
        String linkdata = "";
        Drops drops = null;
        if(PTheader.trim().length() > 0) {
            if(type == 1) {
                log.info(docnum);
                 drops = dropsRepository.findByDocnum(docnum);
                log.info("1");
                linkdata = drops.getPtlink();
                log.info(linkdata);

            }
            else if(type == 4) {
                log.info(docnum);
                drops =   dropsRepository.findByDocnum(docnum);
                log.info("4");
                linkdata = drops.getPtlink();
                log.info(linkdata);
            }
            else {
                PickUP pikcup= pickupRepository.findByDocnum(docnum);
                log.info("2");
                linkdata = pikcup.getPtlink();
                log.info(linkdata);
            }
            if(linkdata.trim().length() > 0){
                String ptlinkdata = linkdata;
                queryStr1 =  MessageFormat.format(UPDATE_PTHEADER_QUERY, dbSchema, "TEXCLOB", "TEXTE_0", "CODE_0", PTheader,ptlinkdata);
                entityManager.createNativeQuery(queryStr1).executeUpdate();

            }
            else {
                if(type == 1){
                    NewTexteCode =   generatePTHeaderLinkDlvy();
                }
                else if(type == 2){
                    NewTexteCode =   generatePTHeaderLinkRecept();
                }
                else if(type == 4){
                    NewTexteCode =   generatePTHeaderLinkPickTckt();
                }
                else if(type == 6){
                    NewTexteCode =   generatePTHeaderLinkPickTckt();
                }
                Date currentDate = new Date();
                String DATENOW = format.format(currentDate);
                log.info("inside PTHeader insert to textclob");
                String query = "INSERT INTO " + dbSchema + ".TEXCLOB\n" +
                        "(UPDTICK_0, " +
                        "CODE_0, " +
                        "TEXTE_0,"+
                        "IDENT1_0,"+
                        "IDENT2_0,"+
                        "IDENT3_0,"+
                        "CREDAT_0, " +
                        "CREUSR_0, " +
                        "CRETIM_0," +
                        "UPDDAT_0, " +
                        "UPDUSR_0, " +
                        "UPDTIM_0," +
                        "CREDATTIM_0, " +
                        "UPDDATTIM_0, " +
                        "AUUID_0)\n" +
                        "VALUES(1, '" + NewTexteCode + "','"+PTheader+"','','','','"+DATENOW+"','RO',0,'"+DATENOW+"','RO',0,'"+DATENOW+"','"+DATENOW+"',0x41027895E7BDB649ADD9317F23ADF47A)";
                entityManager.createNativeQuery(query).executeUpdate();

                this.insertPTHeaderText(NewTexteCode,PTheader,type,docnum);
            }
        }
    }

    private void insertPTHeaderText(String code, String DAta,int type,String docno) {
        log.info("inside ptheader update to doc");
        if(type == 1) {
            String  queryStr2 =  MessageFormat.format(UPDATE_DOC_AFTER_PTHEADER, dbSchema, "SDELIVERY", "PRPTEX1_0", code,"SDHNUM_0",docno);
            entityManager.createNativeQuery(queryStr2).executeUpdate();

        }else if(type == 4) {
            String  queryStr2 =  MessageFormat.format(UPDATE_DOC_AFTER_PTHEADER, dbSchema, "STOPREH", "PRPTEX1_0", code,"PRHNUM_0",docno);
            entityManager.createNativeQuery(queryStr2).executeUpdate();


        }else if(type == 2) {
            String  queryStr2 =  MessageFormat.format(UPDATE_DOC_AFTER_PTHEADER, dbSchema, "XX10CREC", "XTEX1_0", code,"XPTHNUM_0",docno);
            entityManager.createNativeQuery(queryStr2).executeUpdate();
        }
    }



    private void insertPTHeaderTexttoBOL(String code,String Data,String bolnum){
        String  queryStr2 =  MessageFormat.format(UPDATE_DOC_AFTER_PTHEADER, dbSchema, "BILLLADH", "BLHTEX_0", code,"BOLNUM_0",bolnum);
        entityManager.createNativeQuery(queryStr2).executeUpdate();

    }

    private void updateRecord(String tableName, String field1, String field2, String filed3, Date currentDate, String docnum) {
        String queryStr = null;
        String date = format.format(currentDate);
        if (StringUtils.isEmpty(filed3)) {
            queryStr = MessageFormat.format(UPDATE_SINGLE_QUERY, dbSchema, tableName, field1, field2, date, docnum);
        } else {
            queryStr = MessageFormat.format(UPDATE_QUERY, dbSchema, tableName, field1, field2, filed3, date, docnum);
        }
        entityManager.createNativeQuery(queryStr).executeUpdate();
    }


    private void CheckSOlistfromPickTicket(String pcktno, String LVSno) {
           log.info(LVSno);
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_SO_FROM_PICKTICKET_CHECK_QUERY, dbSchema, "STOPRED", pcktno)).getResultList();
            if (list.size() > 0) {
               // entityManager.createNativeQuery(MessageFormat.format(UPDATE_doc_QUERY_AFTER_TRIP_DELETION, dbSchema, "STOPREH", itemCode,"",8,"XX10C_LICPLA_0","ETA_0","ETD_0","BPTNUM_0","DRIVERID_0","XTRAILER_0","XROUTNBR_0","ARVDAT_0","DPEDAT_0","XCOMMENT_0")).executeUpdate();
                    for(int i=0 ; i<list.size();i++){
                        log.info(list.get(i).toString());
                        String queryStr = null;
                        queryStr = MessageFormat.format(UPDATE_NextTrip_QUERY, dbSchema, "SORDER", "XSHLVSNUM_0", "SOHNUM_0", LVSno, list.get(i));
                        entityManager.createNativeQuery(queryStr).executeUpdate();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private int getDocType(String docType) {
        if (!StringUtils.isEmpty(docType)) {
            if ("DLV".equalsIgnoreCase(docType)) {
                return 1;
            } else if ("PICK".equalsIgnoreCase(docType)) {
                return 4;
            } else if ("PRECEIPT".equalsIgnoreCase(docType)) {
                return 2;
            }else if ("RETURN".equalsIgnoreCase(docType)) {
                return 3;
            }
            else if ("MISCPICK".equalsIgnoreCase(docType)) {
                return 6;
            }
            else if ("MISCDROP".equalsIgnoreCase(docType)) {
                return 6;
            }
        }
        return 0;
    }

    private  void insertEquipment(String lvscode,String date,int sequenceNUm, Map<String, Object> map){
        log.info("inside X10CITMDET equipment insert");

        String sql = "INSERT INTO " + dbSchema + ".X10CITMDET\n" +
                "(UPDTICK_0, XLVSNUM_0, XLINENO_0, XITMREF_0, XEQUIPTYPE_0,XQTY_0,XUOM_0,XBPCNUM_0,XSERNUM_0,XSTATUS_0,XSCSNUM_0, CREDATTIM_0, UPDDATTIM_0, AUUID_0, CREUSR_0, UPDUSR_0, XFCY_0,XCHKINQTY_0)\n" +
                "VALUES(1, '" + lvscode + "', " + ((sequenceNUm - 1) * 1000) + ", '" + map.get("xequipid") + "', '" + map.get("xequiptyp") + "',"+ map.get("quantity")+",'','','',0,'','" + date + "', '" + date + "', 0, ' ', ' ','" + map.get("xfcy") + "',0)";
        entityManager.createNativeQuery(sql).executeUpdate();

    }


    private void insertTrip(String itemCode, Map<String, Object> map, String date, int sequenceNUm, int pickDrop, int docType, String StartDate, String endDate) {
        log.info("inside xx10cplanchd");
        int xtemp = pickDrop + 1 ;
        String sql = "INSERT INTO " + dbSchema + ".XX10CPLANCHD\n" +
                "(UPDTICK_0, XNUMPC_0, XLINPC_0, SDHNUM_0, XDTYPE_0, CREDAT_0, CREUSR_0, UPDUSR_0, UPDDAT_0, SEQUENCE_0, FROMPREVDIST_0, FROMPREVTRA_0, ARRIVEDATE_0, AARRIVEDATE_0, ARRIVETIME_0, AARRIVETIME_0, DEPARTDATE_0, ADEPARTDATE_0, DEPARTTIME_0, ADEPARTTIME_0, ARRIVEDATEUT_0, ARRIVETIMEUT_0, DEPARTDATEU_0, DEPARTTIMEU_0, CREDATTIM_0, UPDDATTIM_0, AUUID_0, RAINONAFF_0, RAINONAFF_1, RAINONAFF_2, RAINONAFF_3, RAINONAFF_4, OPTISTA_0, XLOADED_0, XACTETA_0, XACTETD_0, XSMSFLG_0, XSDHSKIP_0, XDEPFLG_0, XDLV_STATUS_0, XMS_0, XVOL_0, XDOCTYP_0, XPICKUP_DROP_0,XDOCSTA_0, XSEALNUM_0, XSKIPRES_0, XACTSEQ_0, RDEPARTDATE_0, RDEPARTTIME_0, RARRIVEDATE_0, RARRIVETIME_0, SERVICETIME_0, XCALCDIS_0, XSPECIFICRES_0, XWAITTIME_0, XMAXSTAHT_0, XLOADBAY_0, SWAITTIME_0, XPICK_SDH_0, XCNFARRDATE_0, XCNFARRTIME_0, XCNFDEPDATE_0, XCNFDEPTIME_0,XACTDISTMTS_0,SERVICETIM_0,XDOCSITE_0,XFRMINDIS_0,XFRMINDTIM_0)\n" +
                "VALUES(1, '" + itemCode + "', " + ((sequenceNUm - 1) * 1000) + ", '" + map.get("docnum") + "', 0, '" + date + "', '', '', '" + date + "', " + sequenceNUm + ", '" + map.get("distance") + "', '" + map.get("time") + "','" + StartDate + "', '', '" + map.get("arrival") + "', '0', '" + endDate + "', '','" + map.get("end") + "' , ' ', '', '0', '', '0', '" + date + "', '" + date + "', 0, ' ', ' ', ' ', ' ', ' ', 2, 0, '0', ' ', 0, 0, 0, 1, ' ', ' ', " + docType + ", " + pickDrop + "," +xtemp + ", ' ', ' ', 0, '', ' ', '', ' ', '" + map.get("serTime") + "', 0.000, ' ', '" + map.get("waitingTime") + "', 0.000, 0, '" + map.get("waitingTime") + "', ' ', '', ' ', '', ' ',0,'" + map.get("serTime") + "','" + map.get("site") + "'," + map.get("individualDistance") + ",'" + map.get("individualTime") +"')";
        entityManager.createNativeQuery(sql).executeUpdate();
    }

    public Map<String, String> saveTrip(List<TripVO> tripVOList) throws Exception {
        try {

            log.info("inside saveTrip",tripVOList);
            for (TripVO tripVO : tripVOList) {

                String docDate = format.format(tripVO.getDate());
                tripVO.setDate(format.parse(docDate));
                if (org.apache.commons.lang3.StringUtils.isBlank(tripVO.getItemCode())) {
                    this.insertTrip(tripVO);
                } else {

                    log.info("inside ELSE- savetrip",tripVO);

                    this.updateTrip(tripVO);
                }
            }
            Map<String, String> map = new HashMap<>();
            map.put("success", "success");
            return map;
        } catch (Exception e) {
            //   entityManager.createNativeQuery("INSERT INTO DEMOTMS.XTMSTEMP VALUES(100);").executeUpdate();

            e.printStackTrace();
            throw e;
        }
    }

    private void unOptimiseRoutes(TripVO tripVO) throws Exception  {
         String code = tripVO.getCode();
         String docDate = format.format(tripVO.getDate());
        int tripno = tripVO.getTrips();
         log.info(code);
         log.info(docDate);
     //   entityManager.createNativeQuery("UPDATE DEMOTMS.XX10TRIPS SET TRIPS='open' where DOCDATE = '' VALUES(100);").executeUpdate();

        String queryStr = null;
        queryStr = MessageFormat.format(UPDATE_SINGLE_QUERY_MULTIPLE_COND, dbSchema, "XX10TRIPS", "optistatus", "Open", "DOCDATE",docDate, "CODE", code,"TRIPS",tripno);

       entityManager.createNativeQuery(queryStr).executeUpdate();
    }

    private void updateTrip(TripVO tripVO) throws Exception {
        log.info("inside updateTrip");
        Trip actualTrip = tripRepository.findByTripCode(tripVO.getItemCode());
       // String vrcode = tripVO.getItemCode().toString();
        if(tripVO.isLockP() == true && tripVO.isLock() == false) {
            log.info("it is for deletion of VR at x3 updateTrip");
            this.deleteTrip(actualTrip.getTripCode());
            this.deleteSOrderData(actualTrip.getTripCode());
           // this.unOptimiseRoutes(tripVO);
        }

    //latest trip list
        List<String> LatestDocList = new ArrayList<>();
        String LtotalObj = mapper.writeValueAsString(tripVO.getTotalObject());
        Map<String, Object> LtripObj = mapper.readValue(LtotalObj, new TypeReference<Map<String, Object>>() {
        });
        String LttObj = mapper.writeValueAsString(LtripObj.get("selectedTripData"));
        List<Map<String, Object>> LtotObj = mapper.readValue(LttObj, new TypeReference<List<Map<String, Object>>>() {
        });
        for (Map<String, Object> Lmap : LtotObj) {
            String docnum= Lmap.get("docnum").toString();
            LatestDocList.add(docnum);
        }
        //delete the prev document if remvoed and udpated
          //actual Trip list
       JSONObject totObj = new JSONObject(actualTrip.getTotalObject());
        JSONArray resultArray = totObj.getJSONArray("selectedTripData");
        List<String> AcutalDocList = new ArrayList<>();
        for (int i = 0; i < resultArray.length(); i++) {
            JSONArray childJsonArray=resultArray.optJSONArray(i);
            JSONObject rec = resultArray.getJSONObject(i);
            String docid = rec.getString("docnum");
            String doctype = rec.getString("doctype");
            if(!LatestDocList.contains(docid)) {
                updateDocumentAfterDeleteDocument(docid , doctype);
            }
            AcutalDocList.add(docid);
        }

            if (Objects.nonNull(actualTrip)) {
                log.info("it is insdie notnull updateTrip");
                List<Map<String, Object>> pickUP = (List<Map<String, Object>>) tripVO.getPickupObject();
                List<Map<String, Object>> dropObject = (List<Map<String, Object>>) tripVO.getDropObject();
                if (pickUP.size() > 0 || dropObject.size() > 0) {
                    this.setTrip(actualTrip, tripVO);
                    tripRepository.save(actualTrip);
                    log.info("it is insdie size updateTrip");
					 this.updateDocumentDetailWhenTripUpdated(tripVO);

                } else {
                    this.updateALlTrips(tripVO.getCode(), tripVO.getTrips(), tripVO.getSite(), tripVO.getDate());
                    int lock = actualTrip.getLock();
                    log.info("lock status inside else ", lock);
                    tripRepository.delete(actualTrip);
                    log.info("after deletion");
                    this.unOptimiseRoutes(tripVO);
                    if (lock == 1) {
                        this.deleteTrip(actualTrip.getTripCode());
                    }
                }
            } else {
                this.insertTrip(tripVO);
            }


        if(tripVO.isReorder() == true || tripVO.isRoute() == false){
            getNextTripofsameVeh(tripVO.getCode(),tripVO.getTrips(),tripVO.getDate());
        }
    }
	
	
	   public void UpdateRemovedDocumentfromTrip(String Docid) {
   }
    public void updateDocumentDetailWhenTripUpdated(TripVO tripVO) {
        try {
      log.info("Update document details when the trip is updated");
       // trip.getGeneratedBy().equalsIgnoreCase("Scheduler")
          int sequenceNUm = 2;
          if (null != tripVO.getTotalObject()) {
              String totalObj = mapper.writeValueAsString(tripVO.getTotalObject());
              Map<String, Object> tripObj = mapper.readValue(totalObj, new TypeReference<Map<String, Object>>() {
              });
              String ttObj = mapper.writeValueAsString(tripObj.get("selectedTripData"));
              List<Map<String, Object>> totObj = mapper.readValue(ttObj, new TypeReference<List<Map<String, Object>>>() {
              });
              for (Map<String, Object> map : totObj) {
                  if (map.size() <= 0) {
                      continue;
                  }
                  int tripno = tripVO.getTrips();
                  List<Map<String, Object>> list = (List<Map<String, Object>>) tripVO.getTrialerObject();
                  String trailer = "",trailer1 = "" ;
                  if (!CollectionUtils.isEmpty(list)) {
                      log.info("after locking", list);
                      Map<String, Object> map2 = list.get(0);
                      log.info("after locking", map2);
                      if (Objects.nonNull(map2.get("trailer"))) {
                          trailer = (String) map2.get("trailer");
                      }
                      if(list.size() > 1) {
                          Map<String, Object> map1 = list.get(1);
                          log.info("after locking", map1);
                          if (Objects.nonNull(map1.get("trailer"))) {
                              trailer1 = (String) map1.get("trailer");
                          }
                      }
                  }
                  Map<String, Object> Vehlist = (Map<String, Object>) tripVO.getVehicleObject();
                  String BPTNUM = "";
                  BPTNUM = (String) Vehlist.get("bptnum");
                  String Veh_code = (String) Vehlist.get("codeyve");
                  String Veh_Licplate = (String) Vehlist.get("name");
                  String driverid = (String) tripVO.getDriverId();
                  String docType = null != map.get("doctype") ? map.get("doctype").toString() : "";
                  int docNum = this.getDocType(docType);
                  // String comments = null != map.get("") ? map.get().toString("") : "";
                  String docDate = format.format(tripVO.getDate());
				    String StartDate = null != map.get("startDate") ? format.format(format.parse(map.get("startDate").toString())) : "";
                  String docendDate = null != map.get("endDate") ? format.format(format.parse(map.get("endDate").toString())) : "";
                String vr = tripVO.getItemCode();
                  String comments = null !=map.get("noteMessage") ? map.get("noteMessage").toString() : "";
                  String dDate = (String) map.get("docdate");
                  Date ddDate = format.parse(dDate);
                  Date selectedDate = format.parse(docDate);
                  if(tripVO.getOptistatus().equalsIgnoreCase("Optimized")) {
                      log.info("Trip is optimised");
                      String rtnDate = format.format(tripVO.getEndDate());
                      Date enddate = format.parse(rtnDate);
                      String Arrtime = null != map.get("arrival") ? map.get("arrival").toString() : "";
                      String customer = null != map.get("bpcode") ?  map.get("bpcode").toString() : "";
                      String Deptime = null != map.get("end") ? map.get("end").toString() : "";
                      String individualDistance = null != map.get("individualDistance") ? map.get("individualDistance").toString() : "";
                      String SevTime = null != map.get("serTime") ? map.get("serTime").toString() : "";
                      String Traveltime = null != map.get("time") ? map.get("time").toString() : "";
                      String TravelDist = null != map.get("distance") ? map.get("distance").toString() : "";
                      String headertext = null != map.get("noteMessage") ? map.get("noteMessage").toString() : "";
                      String carriertext = null != map.get("CarrierMessage") ? map.get("CarrierMessage").toString() : "";
                      String loadertext = null != map.get("loaderMessage") ? map.get("loaderMessage").toString() : "";
                      this.updateDocs(vr, Veh_code, docDate, Arrtime, BPTNUM, Deptime, docNum, map.get("docnum").toString(), driverid, tripno, trailer, comments, headertext, carriertext, loadertext, customer, sequenceNUm,StartDate,docendDate, individualDistance);
                  }
                  else {
                       log.info("Trip is updating");
                      this.updateDocsAtTripCreation(vr, Veh_code, docDate, docNum, map.get("docnum").toString(),tripno, sequenceNUm, Veh_Licplate);
                  }
                  sequenceNUm++;
                  /*
                  String docType = null != map.get("doctype") ? map.get("doctype").toString() : "";
                  int docNum = this.getDocType(docType);
                  String docDate = format.format(tripVO.getDate());
                  String dDate = (String) map.get("docdate");
                  String vr = tripVO.getItemCode();
                  String Veh_code = tripVO.getCode();
                  Date ddDate = format.parse(dDate);
                  Date selectedDate = format.parse(docDate);
                  this.updateDocsAtTripCreation(vr, Veh_code, docDate, docNum, map.get("docnum").toString());
                   */
              }
          }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	

    public void insertTrip(TripVO tripVO) throws Exception {
        log.info("insert Trip  is loaded...");

        List<Map<String, Object>> pickUP = (List<Map<String, Object>>) tripVO.getPickupObject();
        List<Map<String, Object>> dropObject = (List<Map<String, Object>>) tripVO.getDropObject();
        if (!StringUtils.isEmpty(tripVO.getCode()) && (pickUP.size() > 0 || dropObject.size() > 0)) {
            String docDate = format.format(tripVO.getDate());
            String date = format.format(new Date());
            String endDate = "";
            if(StringUtils.isEmpty(tripVO.getEndDate())){
               endDate = "";
            }
            else
            {
              endDate = format.format(tripVO.getEndDate());
            }
            Trip trip = new Trip();
            this.setTrip(trip, tripVO);
            this.generateVRCode(tripVO.getSite(), tripVO.getDate(), trip);
            log.info("inside xx10trips");
            String sql = "INSERT INTO " + dbSchema + ".XX10TRIPS\n" +
                    "(CODE, DRIVERNAME, TRAILERS, EQUIPMENTS, TRIPS, PICKUPS, PICKUPOBJECT, DROPOBJECT, EQUIPMENTOBJECT, TRAILEROBJECT, DROPS, STOPS, SITE, DOCDATE, CREATEDATE, UPDATEDATE, USERCODE, " +
                    "TRIPCODE, TOTALOBJECT, lock, driverId, notes,optistatus,uomTime,serviceTime,totalTime,travelTime,serviceCost,distanceCost,totalCost,fixedCost,uomDistance,totalDistance, weightPercentage, volumePercentage, totalWeight, totalVolume, startTime, endTime, capacity, startIndex, VEHICLEOBJECT, HEUEXEC,DATEXEC,regularCost, overtimeCost,DEPSITE,ARRSITE,ENDDATE,LOADERINFO,FORCESEQ,VRSEQ, GENERATEDBY, alertflg, warningnotes, PER_CAPACITY,PER_VOLUME,TOT_CAPACITY,TOT_VOLUME, DOC_CAPACITY,DOC_VOLUME, UOM_CAPACITY, UOM_VOLUME, XSDEPOT, XDDEPOT)\n" +
                    "VALUES('" + trip.getCode() + "', '" + trip.getDriverName() + "', " + trip.getTrailers() + ", " + trip.getEquipments() + ", " + trip.getTrips() + ", " + trip.getPickups() + ", '" + trip.getPickup() + "', '" + trip.getDrop() + "'," +
                    " '" + trip.getEquipment() + "', '" + trip.getTrialer() + "', " + trip.getDrops() + ", " + trip.getStops() + ", '" + trip.getSite() + "', '" + docDate + "', '" + date + "', '', '', '" + trip.getTripCode() + "', '" + trip.getTotalObject() + "', 0, '" + trip.getDriverId() + "'," +
                    " '" + trip.getNotes() + "', '" + trip.getOptistatus() + "','" + trip.getUomTime() + "','" + trip.getServiceTime() + "','" + trip.getTotalTime() + "','" + trip.getTravelTime() + "','" + trip.getServiceCost() + "','" + trip.getDistanceCost() + "','" + trip.getTotalCost() + "','" + trip.getFixedCost() + "','" + trip.getUomDistance() + "','" + trip.getTotalDistance() + "','" + trip.getWeightPercentage() + "', '" + trip.getVolumePercentage() + "', '" + trip.getTotalWeight() + "', '" + trip.getTotalVolume() + "', '" + trip.getStartTime() + "', '" + trip.getEndTime() + "', '" + trip.getCapacities() + "', '" + trip.getStartIndex() + "', '" + trip.getVehicle() + "','" + trip.getHeuexec() + "','" +date+ "','" + trip.getRegularCost() + "','" + trip.getOvertimeCost() + "','" + trip.getDepSite() + "','" + trip.getArrSite() +"', '" + endDate + "','" + trip.getLoaderInfo() +"', "+trip.getForceSeq()+","+trip.getVrseq()+",'" + trip.getGeneratedBy() +"',0 , '','" + trip.getPer_capacity() + "', '" + trip.getPer_volume() + "','" + trip.getTot_capacity() + "', '" + trip.getTot_volume() + "','" + trip.getDoc_capacity() + "', '" + trip.getDoc_volume() + "','" + trip.getUom_capacity() + "', '" + trip.getUom_volume() + "', '" + trip.getXsdepot() + "','" + trip.getXddepot() + "'  )";
            entityManager.createNativeQuery(sql).executeUpdate();
			
			  int sequenceNUm = 2;

            Map<String, Object> Vehlist = (Map<String, Object>) tripVO.getVehicleObject();
            String BPTNUM = "";
            BPTNUM = (String) Vehlist.get("bptnum");
            String Veh_code1 = (String) Vehlist.get("codeyve");
            String Veh_Licplate = (String) Vehlist.get("name");



            if (null != tripVO.getTotalObject()) {
                   String totalObj = mapper.writeValueAsString(tripVO.getTotalObject());
                   Integer tripno = trip.getTrips();
                   Map<String, Object> tripObj = mapper.readValue(totalObj, new TypeReference<Map<String, Object>>() {
                   });
                   String ttObj = mapper.writeValueAsString(tripObj.get("selectedTripData"));
                   List<Map<String, Object>> totObj = mapper.readValue(ttObj, new TypeReference<List<Map<String, Object>>>() {
                   });
                   for (Map<String, Object> map : totObj) {
                       if (map.size() <= 0) {
                           continue;
                       }
                       String docType = null != map.get("doctype") ? map.get("doctype").toString() : "";
                       int docNum = this.getDocType(docType);
                       String dDate = (String) map.get("docdate");
                       String vr = trip.getTripCode();
                       String Veh_code = trip.getCode();
                       Date ddDate = format.parse(dDate);
                       Date selectedDate = format.parse(docDate);
                       this.updateDocsAtTripCreation(vr, Veh_code, docDate, docNum, map.get("docnum").toString(),tripno, sequenceNUm, Veh_Licplate);
                       sequenceNUm ++;
                   }
               }
            // }
            

        } else {
            throw new Exception("Trip doesn't have drops or pickups");
        }
    }



    private void updateALlTrips(String tripCode, int tripCount, String site, Date date) {
        List<Trip> updateTrips = new ArrayList<>();
        List<Trip> trips = tripRepository.findBySiteAndDocdate(site, date);
        for(Trip trip: trips) {
            if(trip.getCode().equalsIgnoreCase(tripCode) && (trip.getTrips() > tripCount)) {
                int tCount = trip.getTrips() - 1;
                trip.setTrips(tCount);
                updateTrips.add(trip);
            }
        }
        tripRepository.saveAll(updateTrips);
    }

    private void calculatePercentages(TripVO tripVO) {

    }

    private void generateVRCode(String site, Date date, Trip currentTrip) {
        List<Trip> trips = tripRepository.findBySiteAndDocdateOrderByTripCodeAsc(site, date);
        int count = 0;
        if (trips.size() > 0) {

            String itemCode = trips.get(trips.size() - 1).getTripCode();
            log.info(itemCode);
            String tripCodeNumber = itemCode.substring(itemCode.length() - 3, itemCode.length());
            String strPattern = "^0+(?!$)";
            String str1 = tripCodeNumber.replaceAll(strPattern, "");
            log.info(str1);
            count = Integer.parseInt(str1);
            String str = String.format("%03d", count + 1);
            log.info(str);
            String Latest_TRIPnumber = MessageFormat.format(TRIP_NUMBER, tripFormat.format(date), site, str);
            log.info(Latest_TRIPnumber);
            currentTrip.setVrseq(Integer.parseInt(str));
            currentTrip.setTripCode(Latest_TRIPnumber);


        }
        else {
            String str = String.format("%03d", count + 1);
            log.info(str);
            String Latest_TRIPnumber = MessageFormat.format(TRIP_NUMBER, tripFormat.format(date), site, str);
            log.info(Latest_TRIPnumber);
            currentTrip.setVrseq(Integer.parseInt(str));
            currentTrip.setTripCode(Latest_TRIPnumber);

            log.info("inside else, no trip exist");

        }
        //to set trip sequence number
        int tripC = 0;
        for(Trip trip: trips){
            if(trip.getCode().equalsIgnoreCase(currentTrip.getCode())){
                tripC = trip.getTrips();
            }
        }
        currentTrip.setTrips(tripC+1);

    }
    private void setTrip(Trip trip, TripVO tripVO) {
        trip.setTripCode(tripVO.getItemCode());
        trip.setCode(tripVO.getCode());
        trip.setDriverName(tripVO.getDriverName());
        trip.setTrailers(tripVO.getTrailers());
        trip.setEquipments(tripVO.getEquipments());
        trip.setTrips(tripVO.getTrips());
        trip.setPickups(tripVO.getPickups());
        trip.setDoc_capacity(tripVO.getDoc_capacity());
        trip.setTot_capacity(tripVO.getTot_capacity());
        trip.setTot_volume(tripVO.getTot_volume());
        trip.setDoc_volume(tripVO.getDoc_volume());
        trip.setPer_volume(tripVO.getPer_volume());
        trip.setPer_capacity(tripVO.getPer_capacity());
        trip.setDrops(tripVO.getDrops());
        trip.setStops(tripVO.getStops());
        trip.setDocdate(tripVO.getDate());
        trip.setUpddattim(new Date());
        trip.setSite(tripVO.getSite());
        trip.setArrSite(tripVO.getArrSite());
        trip.setDepSite(tripVO.getDepSite());
        trip.setDriverId(tripVO.getDriverId());
		trip.setGeneratedBy(tripVO.getGeneratedBy());
        trip.setStartTime(tripVO.getStartTime());
        trip.setTotalDistance(tripVO.getTotalDistance());
        trip.setTotalTime(tripVO.getTotalTime());
        trip.setEndTime(tripVO.getEndTime());
        trip.setEndDate(tripVO.getEndDate());
        trip.setHeuexec(tripVO.getHeuexec());
        trip.setDatexec(tripVO.getDatexec());
        if(org.apache.commons.lang3.StringUtils.isNotBlank(tripVO.getNotes())) {
            trip.setNotes(tripVO.getNotes());
        }else {
            trip.setNotes(org.apache.commons.lang3.StringUtils.EMPTY);
        }
        trip.setWeightPercentage(tripVO.getWeightPercentage());
        trip.setVolumePercentage(tripVO.getVolumePercentage());
        trip.setTotalWeight(tripVO.getTotalWeight());
        trip.setTotalVolume(tripVO.getTotalVolume());
        trip.setStartTime(tripVO.getStartTime());
        trip.setEndTime(tripVO.getEndTime());
        trip.setCapacities(tripVO.getCapacities());
        trip.setStartIndex(tripVO.getStartIndex());
        trip.setOptistatus(tripVO.getOptistatus());
        trip.setUomTime(tripVO.getUomTime());
        trip.setTotalTime(tripVO.getTotalTime());
        trip.setTravelTime(tripVO.getTravelTime());
        trip.setServiceTime(tripVO.getServiceTime());
        trip.setServiceCost(tripVO.getServiceCost());
        trip.setDistanceCost(tripVO.getDistanceCost());
        trip.setTotalCost(tripVO.getTotalCost());
        trip.setFixedCost(tripVO.getFixedCost());
        trip.setOvertimeCost(tripVO.getOvertimeCost());
        trip.setRegularCost(tripVO.getRegularCost());
        trip.setUomDistance(tripVO.getUomDistance());
        trip.setTotalDistance(tripVO.getTotalDistance());
        trip.setLoaderInfo(tripVO.getLoaderInfo());
        trip.setXsdepot(tripVO.getArrivalDepot());
        trip.setXddepot(tripVO.getDepartureDepot());

        if(tripVO.isForceSeq()){
            trip.setForceSeq(1);
        }
        else{
            trip.setForceSeq(0);
        }
        if(tripVO.isLock()) {
            trip.setLock(1);
        }else {
            trip.setLock(0);
        }
        try {
      //      this.restrictsOtherDocuments(tripVO);
            String pickUp = mapper.writeValueAsString(tripVO.getPickupObject());
            String drop = mapper.writeValueAsString(tripVO.getDropObject());
            String equipment = mapper.writeValueAsString(tripVO.getEquipmentObject());
            String trailer = mapper.writeValueAsString(tripVO.getTrialerObject());
            String totalObject = mapper.writeValueAsString(tripVO.getTotalObject());
            String vehicle =  mapper.writeValueAsString(tripVO.getVehicleObject());
            trip.setPickup(pickUp.replaceAll("'", "''"));
            trip.setDrop(drop.replaceAll("'", "''"));
            trip.setEquipment(equipment.replaceAll("'", "''"));
            trip.setTrialer(trailer.replaceAll("'", "''"));
            trip.setTotalObject(totalObject.replaceAll("'", "''"));
            trip.setVehicle(vehicle.replaceAll("'", "''"));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void restrictsOtherDocuments(TripVO tripVO) {
        Map<String, Object> totalMap = (Map<String, Object>)  tripVO.getTotalObject();
        List<Map<String, Object>> list = (List<Map<String, Object>>) totalMap.get("selectedTripData");
        List<Map<String, Object>> selectedList = new LinkedList<>();
        if(!CollectionUtils.isEmpty(list)) {
            boolean documentStarted = false;
            for(Map<String, Object> map: list) {
                if(Objects.nonNull(map) && map.size() > 0) {
                    documentStarted = true;
                    String vehicleCode = (String) map.get("vehicleCode");
                    if(tripVO.getCode().equalsIgnoreCase(vehicleCode)) {
                        selectedList.add(map);
                    }
                }else {
                    if(!documentStarted) {
                        selectedList.add(map);
                    }
                }
            }
        }
        totalMap.put("selectedTripData", selectedList);
        tripVO.setTotalObject(totalMap);
    }

    private int getCount() {
        String sql = "select count(*) from "+dbSchema+".XX10TRIPS";
        int count = (int) entityManager.createNativeQuery(sql).getSingleResult();
        return count++;
    }


    private String getNextTripofsameVeh(String Vehicle , int tripindex,Date date){
        String docDate = format.format(date);
       //  String sql = "select TRIPCODE from "+dbSchema+".XX10TRIPS where TRIPS >"+tripindex+" and CODE ="+Vehicle+" and DOCDATE="+docDate;
      //  Query query = entityManager.createNativeQuery(sql);

        List<String> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIPS_GRTTHAN_QUERY, dbSchema, "XX10TRIPS",tripindex,Vehicle, docDate)).getResultList();
        if (list.size() > 0) {

            for(int i=0 ; i<list.size();i++){
                String queryStr = null;
                queryStr = MessageFormat.format(UPDATE_NextTrip_QUERY, dbSchema, "XX10TRIPS", "optistatus",  "TRIPCODE","Open", list.get(i));
                entityManager.createNativeQuery(queryStr).executeUpdate();

            }
    }
        /*
        List<String> tripList = query.getResultList();


        if(tripList.size() > 0){
            log.info("inside length checking");
            for(int i=0 ; i<tripList.size();i++){

            } }
*/


            return "";
    }

    public List<TripVO> getTrips(List<String> site, Date date) {

        return this.cacheService.getTrips(site, date);
    }
	
	 public List<TripVO> getTripsWithRange(List<String> site, Date sdate, Date edate) {
        return this.cacheService.getTripswithRange(site, sdate, edate);
    }


    //test for multiple site
    public List<Vehicle> getVehiclebySite(List<String> sites, Date date) {
         List<Vehicle> Vehlist = null;
         String dddate = format.format(date);
        Vehlist =  vehicleRepository.findBySitesAndDate(sites,dddate);
            return Vehlist;
    }


    public void deleteTrip(List<TripVO> tripVOList) {
        try {
            log.info("INSIDE deleteTrip");
            TripVO tripVO = tripVOList.get(0);
            this.deletesingleTrip(tripVO.getItemCode());
        } catch (Exception e) {
        e.printStackTrace();
    }
}

  public void UpdatedeletedDocument(List<DocsVO> docsList) {
        log.info("inside updatedelteddoc");
        try {
            if (docsList.size() > 0) {
                for (DocsVO doc : docsList) {
                    this.updateDocumentAfterDeleteDocument(doc.getDocnum(), doc.getDoctype());
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        }



    public List<Trip_ReportVO> getTripsList(Date date) {

        return this.cacheService.getTripsList(date);
    }
	
	
	

    //allcation for pick tickets
    public void SubmitforAlocation (List<LVSAllocationVO> PickTicketList) {
        try {
            for(LVSAllocationVO tripvo : PickTicketList) {
              //  List<LVSAllocation> tempVO = new ArrayList<>();
              //  tempVO.add(tripvo);
                AllocatePick(tripvo);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
	
	
	   //warning Alert off
    public void DeleteAllocatedDataifPickTicketExist2 (LVSAllocationVO pickticket) {
        try {
           // log.info("INSIDE warningAlert off");
          //  LVSAllocationVO tripVO = pickticket.get(0);
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_ALLOCATION_QUERY, dbSchema, "X1CPICKDET", pickticket.getPrhnum(), pickticket.getLineno())).getResultList();
            //  List<TripVO>  list1 = entityManager.createNativeQuery(MessageFormat.format(SELECT_SINGLETRIP_QUERY, dbSchema, "XX10TRIPS", itemCode)).getResultList();

            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_ALLOCATED_QUERY, dbSchema, "X1CPICKDET", pickticket.getPrhnum(), pickticket.getLineno())).executeUpdate();

            }

        } catch (Exception e) {
            e.printStackTrace();

        }
    }






public void DeleteAllocatedDataifPickTicketExist(LVSAllocationVO pickticket) {
    try {
        // Construct query dynamically with schema and table name
        String query = SELECT_ALLOCATION_QUERY_UPD
            .replace("{schema}", dbSchema)
            .replace("{table}", "X1CPICKDET");

        // Execute the select query
        List<Object> list = entityManager.createNativeQuery(query)
            .setParameter("prhnum", pickticket.getPrhnum())
            .setParameter("prelin", pickticket.getLineno())
            .getResultList();

        // If data exists, execute the delete query
        if (!list.isEmpty()) {
            String deleteQuery = "DELETE FROM {schema}.{table} WHERE PRHNUM_0 = :prhnum AND PRELIN_0 = :prelin"
                .replace("{schema}", dbSchema)
                .replace("{table}", "X1CPICKDET");

            entityManager.createNativeQuery(deleteQuery)
                .setParameter("prhnum", pickticket.getPrhnum())
                .setParameter("prelin", pickticket.getLineno())
                .executeUpdate();
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}










    private void AllocatePick(LVSAllocationVO pickticket) {
        Date currentDate = new Date();
        String date = format.format(currentDate);
		
		  // check and delete if pick tickets exist
        this.DeleteAllocatedDataifPickTicketExist(pickticket);
		
		

        String query = "INSERT INTO " + dbSchema + ".X1CPICKDET\n" +
                "(UPDTICK_0, " +
                "PRHNUM_0, " +
                "PRELIN_0, " +
                "STOFCY_0, " +
                "STOCOU_0, " +
                "ITMREF_0, " +
                "QTY_0, " +
                "LOT_0, " +
                "STAFLAG_0, " +
                "DAT_0, " +
                "CREDATTIM_0, " +
                "UPDDATTIM_0, " +
                "AUUID_0, " +
                "CREUSR_0, " +
				 "XNUMPC_0, "+
                "UPDUSR_0 )\n" +
        "VALUES (1,'" + pickticket.getPrhnum() + "',"+pickticket.getLineno()+",'" + pickticket.getSite() + "',"+pickticket.getCount()+",'" + pickticket.getProd() + "',"+pickticket.getQty()+",'"+pickticket.getLot()+"',0,'"+date+"','"+date+"','"+date+"',0,'RO','"+ pickticket.getVrnum() + "','RO')";
        entityManager.createNativeQuery(query).executeUpdate();



    }
	

	
    private void updateSOrderData(String itemCode, String Docnum) {
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_SORDERH_QUERY2, dbSchema, "X10CSOH", itemCode, "XTPTHNUM_0", Docnum)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_SORDERH_QUERY, dbSchema, "X10CSOH", itemCode, "XTPTHNUM_0", Docnum ,"" )).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	
	// Group 2
	
	
    public void deleteOpenDocs(List<OpenDocsVO> OpenDocsList) {
        try {
            log.info("INSIDE deleteTrip");
            for(OpenDocsVO openDocsVO : OpenDocsList) {
                 log.info("inside for");
                log.info(openDocsVO.getDocnum());
                this.deletesingleOpenDocs(openDocsVO.getDocnum(), openDocsVO);



            }

        //    deleteVRTrip(actualtrip.getTripCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void deletesingleOpenDocs(String itemCode, OpenDocsVO tripObject) throws JsonProcessingException {

        log.info("inside delete OpenDocs");
     try {
        Trip actualtrip = new Trip();
         actualtrip =   tripRepository.findByTripCode(tripObject.getVrcode());

          log.info(String.valueOf(actualtrip.getStops()));
         if (actualtrip.getStops() > 1) {

             JSONObject totObj = new JSONObject(actualtrip.getTotalObject());
             JSONArray resultArray = totObj.getJSONArray("selectedTripData");
             JSONArray updateresultArray = new JSONArray();
             List<String> AcutalDocList = new ArrayList<>();
             int updateStops = actualtrip.getStops();
             int updateDocs = actualtrip.getDrops();
                     int updatedTot_capacity, updatedTot_volume;

             for (int i = 0; i < resultArray.length(); i++) {
                 JSONArray childJsonArray = resultArray.optJSONArray(i);
                 JSONObject rec = resultArray.getJSONObject(i);
                 String docid = rec.getString("docnum");
                 String doctype = rec.getString("doctype");


                 if (tripObject.getDocnum().equalsIgnoreCase(docid)) {
                     updateStops = actualtrip.getStops() - 1;
                     updateDocs = actualtrip.getDrops() - 1;

                 } else {
                     updateresultArray.put(resultArray.getJSONObject(i));
                 }


             }
             totObj.put("selectedTripData", updateresultArray);
              log.info("after modified");
             log.info(String.valueOf(updateStops));
             entityManager.createNativeQuery(MessageFormat.format(UPDATE_TRIP_AFTER_DOC_DELETE, dbSchema, "XX10TRIPS", "TOTALOBJECT", totObj , "STOPS", updateStops , "DROPS" , updateDocs, "TRIPCODE" , actualtrip.getTripCode() )).executeUpdate();




         }
         else {
             // delete entire route
             entityManager.createNativeQuery(MessageFormat.format(DELTE_SINGLETRIP_QUERY, dbSchema, "XX10TRIPS", actualtrip.getTripCode())).executeUpdate();

         }

         //update Vehicle, Driver , Route details for PT
         updateDocumentAfterDeleteDocument( tripObject.getDocnum(), "PICK");


         if(actualtrip.getLock() == 1) {

             deleteVRTrip(actualtrip.getTripCode());
         }
         updateSOrderData(actualtrip.getTripCode(), tripObject.getDocnum());


     } catch(Exception e){
        e.printStackTrace();
    }
    }

    private void deleteVRTrip(String itemCode) {

        log.info("inside delete Trip");
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHA", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_TRIP_QUERY, dbSchema, "XX10CPLANCHA", itemCode)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_TRIP_QUERY, dbSchema, "XX10CPLANCHD", itemCode)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(DELTE_TRIP_QUERY, dbSchema, "XX10CPLANCHD", itemCode)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
	
	
	
	    public TripVO getTripDetailsByVRCode(String vrcode) {

        return cacheService.getTripByVrcode(vrcode);
    }



    public void addDocstoRoutes(List<OpenDocsRoutesVO> tripVOList) {
        try {
            log.info("INSIDE addDocstoRoutes");
            for(OpenDocsRoutesVO tripVO: tripVOList) {
                this.checkAndDocstoRoute(tripVO.getTripCode(), tripVO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void checkAndDocstoRoute(String itemCode, OpenDocsRoutesVO tripObject) {

        log.info("inside delete Trip");
        log.info(itemCode);
        try {

            List<SorderList> OpenDocsSL = sorderListRepository.getOpenDocsSObyRouteCode(itemCode);



            if (OpenDocsSL.size() > 0) {
                log.info("inside delete Trip 2");
                for (SorderList sorder : OpenDocsSL) {
                   // this.updateDocumentAfterDeleteDocument(doc.getDocnum(), doc.getDoctype());
                    log.info(sorder.getSohnum());
                     List<PcktsBySO>  pobj = pcktsBySORepository.findBySorder(sorder.getSohnum());
                    log.info("inside delete Trip 3");
                     if(pobj.size() > 0) {
                         log.info("inside delete Trip 4");
                         this.InsertOpenDocsforRoute(pobj, itemCode,sorder.getSeqno(), tripObject, sorder.getSohnum());
                     }
                    }

                }
            log.info("inside delete Trip 5");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("inside delete Trip 6");
    }

    private  void InsertOpenDocsforRoute(List<PcktsBySO> PSO , String routeCode,int seqno, OpenDocsRoutesVO tObject, String Sorder) {
        log.info("inside delete Trip 2 1");
        try {
            for (PcktsBySO data : PSO) {
                log.info("inside delete Trip 2 2");
                log.info(data.getPrhnum());
                //  Docs docs = docsRepository.findByDocnum(data.getPrhnum());

                 Docs doc = docsRepository.findByDocnum(data.getPrhnum());
                 DocsVO tempDocVO = this.convertDocs(doc,tObject ,seqno);

                ObjectMapper objectMapper = new ObjectMapper();

                String jsonDocsVO = objectMapper.writeValueAsString(tempDocVO);
                objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
                JsonNode jsonNodeObject = objectMapper.readTree(jsonDocsVO);

                // You can also convert it to an ObjectNode if needed
            //    ObjectNode objectNode = objectMapper.readValue(json, ObjectNode.class);

                JSONArray resultArray2 = new JSONArray();

                // Add some initial elements to the array
                JSONObject jsonObjectval = new JSONObject(jsonDocsVO);





              //  DocsVO docsVO = asyncSchdulerService.getDocInfofromDocnum(data.getPrhnum());


                Trip actualtrip = tripRepository.findByTripCode(routeCode);
                int tempseq = seqno - 2;
                log.info(String.valueOf(actualtrip.getStops()));
                if (actualtrip.getStops() > 0) {

                    JSONObject totObj = new JSONObject(actualtrip.getTotalObject());
                    JSONArray resultArray = totObj.getJSONArray("selectedTripData");
                    //   JSONArray updateresultArray = resultArray;

                    JSONArray updateresultArray =  resultArray;

                    for (int i = updateresultArray.length() - 1; i >= tempseq; i--) {
                        JSONObject element = updateresultArray.getJSONObject(i);

                        updateresultArray.put(i + 1, element);
                    }



                    updateresultArray.put(tempseq, jsonObjectval);
                    int updateStops = actualtrip.getStops() + 1;
                    int updateDocs = actualtrip.getDrops() + 1;
                    int updatedTot_capacity, updatedTot_volume;

                    totObj.put("selectedTripData", updateresultArray);
                    String temptotObj = totObj.toString();
                    String escapedString = temptotObj.replaceAll("'", "''");
                    log.info("after modified");
                    log.info(String.valueOf(updateStops));
                 //   entityManager.createNativeQuery(MessageFormat.format(UPDATE_TRIP_AFTER_DOC_DELETE, dbSchema, "XX10TRIPS", "TOTALOBJECT", escapedString, "STOPS", updateStops, "DROPS", updateDocs, "TRIPCODE", actualtrip.getTripCode())).executeUpdate();

                    this.updateTripAfterdata(escapedString, actualtrip, updateStops, updateDocs);

                   this.updateDocumentDetailsAfterDocAddedToTrip(tempDocVO.getDocnum(),tObject ,seqno, "PS2");

                   this.updateSOrderDataAfterPKAdded(actualtrip.getTripCode(), tempDocVO.getDocnum(), Sorder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTripAfterdata(String escapedString, Trip tempactualtrip, int stops, int drops) {

        tempactualtrip.setDrops(drops);
        tempactualtrip.setStops(stops);
        tempactualtrip.setTotalObject(escapedString);
        tripRepository.save(tempactualtrip);

    }

    private void updateSOrderDataAfterPKAdded(String itemCode, String Docnum, String sorder) {

        try {
            List<Object> list = entityManager.createNativeQuery(MessageFormat.format(SELECT_SORDERH_QUERY2, dbSchema, "X10CSOH", itemCode,"XTSOHNUM_0", sorder)).getResultList();
            if (list.size() > 0) {
                entityManager.createNativeQuery(MessageFormat.format(UPDATE_SORDERH_QUERY, dbSchema, "X10CSOH", itemCode, "XTSOHNUM_0", sorder, Docnum)).executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void updateDocumentDetailsAfterDocAddedToTrip(String docnum,OpenDocsRoutesVO tObject , int seqno, String licplate) {
        String docDate = format.format(tObject.getDocdate());

        this.updatedocumentsforSchduler("STOPREH", "XX10C_NUMPC_0", "XX10C_LICPLA_0", "PRHNUM_0", "XDLV_STATUS_0",  tObject.getTripCode(), tObject.getCode(), docDate, docnum, 1 , "DLVDAT_0", "XROUTNBR_0",tObject.getTrips(),"XSEQUENCE_0",seqno , "SHIDAT_0", "TRLLICPLATER_0", licplate );


    }



    private DocsVO convertDocs(Docs drops, OpenDocsRoutesVO tripObject, int seq) {
        DocsVO dropsVO = new DocsVO();
        log.info("conversions");
        log.info(tripObject.getCode());
     //   JSONObject jsonObject = new JSONObject();
        dropsVO.setDocnum(this.convertToString(drops.getDocnum()));
        dropsVO.setMiscpickflg(drops.getMiscpickflg());
        dropsVO.setDoctype(this.convertToString(drops.getDoctype()));
        dropsVO.setMovtype(this.convertToString(drops.getMovtype()));
        dropsVO.setRouteTag(this.convertToString(drops.getRouteTag()));
        dropsVO.setRouteColor(this.convertToString(drops.getRouteColor()));
        dropsVO.setVehClassAssoc(this.convertToString(drops.getVehClassAssoc()));
        dropsVO.setRouteTagFRA(this.convertToString(drops.getRouteTagFRA()));
        dropsVO.setRouteCode(this.convertToString(drops.getRouteCode()));
        dropsVO.setRouteBgColor(this.convertToString(drops.getRouteBgColor()));
        dropsVO.setRouteCodeDesc(this.convertToString(drops.getRouteCodeDesc()));
        dropsVO.setDocdate(this.convertToString(drops.getDocdate()));
        dropsVO.setPrelistCode(this.convertToString(drops.getPrelistCode()));
        dropsVO.setDlvystatus(this.convertToString('1'));
        dropsVO.setDocinst(this.convertToString(drops.getDocinst()));
        dropsVO.setPtlink(this.convertToString(drops.getPtlink()));
        dropsVO.setDeptime(this.convertToString(drops.getDeptime()));
        dropsVO.setArvtime(this.convertToString(drops.getArvtime()));
        dropsVO.setPtheader(this.convertToString(drops.getPtheader()));
        dropsVO.setPoscode(this.convertToString(drops.getPoscode()));
        dropsVO.setCity(this.convertToString(drops.getCity()));
        dropsVO.setTrailer(this.convertToString(drops.getTrailer()));
        dropsVO.setCarrier(this.convertToString(drops.getCarrier()));
        dropsVO.setDrivercode(this.convertToString(tripObject.getDriverId()));
        dropsVO.setVehicleCode(this.convertToString(tripObject.getCode()));
        dropsVO.setBpcode(this.convertToString(drops.getBpcode()));
        dropsVO.setBpname(this.convertToString(drops.getBpname()));
        dropsVO.setAdrescode(this.convertToString(drops.getAdrescode()));
        dropsVO.setAdresname(this.convertToString(drops.getAdresname()));
        dropsVO.setSite(this.convertToString(drops.getSite()));
        if(Objects.nonNull(drops.getCarrierColor())) {
            dropsVO.setCarrierColor(this.convertToString(drops.getCarrierColor()));
        }else {
            dropsVO.setCarrierColor(";font-style:normal;background-color:#92a8d1");
        }

       dropsVO.setGroupingColor(";font-style:normal;background-color:#92a8d1");

        dropsVO.setNetweight(this.convertToBigDecimal(drops.getNetweight()));
        dropsVO.setWeightunit(this.convertToString(drops.getWeightunit()));
        dropsVO.setVolume(this.convertToBigDecimal(drops.getVolume()));
        dropsVO.setVolume_unit(this.convertToString(drops.getVolume_unit()));
        dropsVO.setLng(this.convertDouble(drops.getGps_x()));
        dropsVO.setLat(this.convertDouble(drops.getGps_y()));
        dropsVO.setTripno(this.convertToString(tripObject.getTrips()));
        dropsVO.setVrcode(this.convertToString(tripObject.getTripCode()));
        dropsVO.setVrseq(this.convertToString(drops.getVrseq()));
        dropsVO.setSeq(this.convertToString(seq));
        dropsVO.setPairedDoc(this.convertToString(drops.getPairedDoc()));
        dropsVO.setServiceTime(this.convertToString(drops.getServiceTime()));
        dropsVO.setWaitingTime(this.convertToString(drops.getWaitingTime()));
        dropsVO.setLoadBay(this.convertToString(drops.getLoadBay()));
        dropsVO.setTailGate(this.convertToString(drops.getTailGate()));
        dropsVO.setVehType(this.convertToString(drops.getVehType()));
        dropsVO.setBPServiceTime(this.convertToString(drops.getBPServiceTime()));
        dropsVO.setStackHeight(this.convertToString(drops.getStackHeight()));
        dropsVO.setTimings(this.convertToString(drops.getTimings()));
        //  dropsVO.setPacking(this.PackingConv((Short)drops.getPacking()));
        // dropsVO.setHeight(this.HeightConv((Short)drops.getHeight()));
        //  dropsVO.setLoadingOrder(this.LoadingOrderConv((Short)drops.getLoadingOrder()));

        // dropsVO.setLogisticDetails(this.constructDropSpeciality(this.convertToString(drops.get("loadBay")),this.convertToString(drops.get("tailGate")),this.DecimaltoString((BigDecimal)drops.get("BPServiceTime")),this.DecimaltoString((BigDecimal) drops.get("WaitingTime")), this.stackHeightConv((BigDecimal) drops.get("StackHeight")),this.convertToString(drops.get("vehType")),this.convertToString(drops.get("Timings")),this.PackingConv((Short)drops.get("Packing")),this.HeightConv((Short) drops.get("Height")),this.LoadingOrderConv((Short)drops.get("LoadingOrder"))));
        //dropsVO.setBPServiceTime(this.convertToString(drops.get("BPServiceTime")));
        if(Objects.isNull(dropsVO.getProducts())) {
            List<DocDs> productVOS = docDsRepository.getprodsbyDocnum(drops.getDocnum());
            List<ProductVO> ProductList = new ArrayList<>();
            if(productVOS.size() > 0) {

                for(DocDs prod : productVOS) {
                    ProductVO doc = null;
                    doc = this.convertProductDocs(prod);

                    ProductList.add(doc);
                }
            }
            dropsVO.setProducts(ProductList);
        }
        //   dropsVO.getProducts().add(getProductVO(drops));

        return dropsVO;
    }

    private ProductVO convertProductDocs(DocDs map) {
        ProductVO productVO = new ProductVO();
        productVO.setProductCode(this.convertToString(map.getProdcode()));
        productVO.setDocLineNum(this.convertToString(map.getDoclineno()));
        productVO.setProductName(this.convertToString(map.getProdname()));
        productVO.setProductCateg(this.convertToString(map.getProdcateg()));
        productVO.setQuantity(this.convertToString(map.getQty()));
        if(null != productVO.getQuantity() && productVO.getQuantity().length() > 4) {
            String quant = productVO.getQuantity().substring(0, productVO.getQuantity().indexOf("."));
            productVO.setQuantity(quant);
        }
        productVO.setUom(this.convertToString(map.getUom()));
        return productVO;
    }

    private Double convertDouble(Object value) {
        if(Objects.nonNull(value)) return Double.parseDouble(value.toString());
        return 0.0;
    }

    private BigDecimal convertToBigDecimal(Object value) {
        if(Objects.nonNull(value)) return (BigDecimal) value;
        return new BigDecimal(0);
    }

	


	



	
}
