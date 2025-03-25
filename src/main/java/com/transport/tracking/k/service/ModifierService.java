package com.transport.tracking.k.service;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transport.tracking.k.service.CacheSchedulerService;
import com.transport.tracking.model.DocDs;
import com.transport.tracking.model.Docs;
import com.transport.tracking.repository.DocDsRepository;
import com.transport.tracking.response.DocsVO;
import com.transport.tracking.model.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.transport.tracking.repository.*;
import com.transport.tracking.response.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
@Transactional
public class ModifierService {


    @Value("${db.schema}")
    private String dbSchema;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private OpenDocsRepository openDocsRepository ;

    @Autowired
    private OpenDocsRoutesRepository openDocsRoutesRepository ;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;



    private String OPENDOCS_QUERY = "select d.*, x.PRODUCTCODE, x.PRODUCTNAME,x.PRODUCTCATEG, x.QUANTITY, x.UOM, x.DOCLINENO \n" +
            " from {0}.XTMSG2DELDOCS d left join {0}.XSCHDOCSD x on d.DOCNUM = x.DOCNUM where {1}";

    private String OPENTOADDDOCS_QUERY = "select d.*, x.PRODUCTCODE, x.PRODUCTNAME,x.PRODUCTCATEG, x.QUANTITY, x.UOM, x.DOCLINENO \n" +
            " from {0}.XTMSG2ADDDOCS d left join {0}.XSCHDOCSD x on d.DOCNUM = x.DOCNUM where {1}";


    private String DOCS_QUERY2 = "select d.*, x.PRODUCTCODE, x.PRODUCTNAME,x.PRODUCTCATEG, x.QUANTITY, x.UOM, x.DOCLINENO \n" +
            " from {0}.XSCHDOCS d left join {0}.XSCHDOCSD x on d.DOCNUM = x.DOCNUM where {1}";

    private static String SITE_DATERANGE = "d.SITE IN {0} AND d.DOCDATE BETWEEN  ''{1}'' AND ''{2}'' order by d.DOCDATE, d.TRIPNO, d.SEQ";

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public String ListtoString(List<String> sites)
    {
        String preparan = "(";
        String ListCommaSeparated = sites.stream()
                .map(String::toUpperCase)
                .map(String->("'"+String+"'"))
                .collect(Collectors.joining(","));

        String postparan = ")";
        String totalString = preparan+ListCommaSeparated+postparan;

        return totalString;
    }

    public List<OpenDocsVO> getOpenDocsWithRange(List<String> site, Date sdate, Date edate) {
        List<OpenDocs> drops = null;
        List<OpenDocsVO> dropsList = new ArrayList<>();
        Map<String, String> paramMap = new HashMap<>();
        List<Map<String, Object>> resultList2 = new ArrayList<>();
        List<OpenDocs> resultList = new ArrayList<>();
        String Sites =  this.ListtoString(site);
        log.info("Sch - inside Range2");
        log.info(site.toString());
        if(!StringUtils.isEmpty(site)) {

            // resultList2 = jdbcTemplate.queryForList(MessageFormat.format(DOCS_QUERY, dbSchema,Sites,dateFormat.format(sdate),dateFormat.format(edate)), paramMap);


            resultList2 = jdbcTemplate.queryForList(MessageFormat.format(OPENDOCS_QUERY, dbSchema,
                    MessageFormat.format(SITE_DATERANGE, Sites, dateFormat.format(sdate),dateFormat.format(edate))), paramMap);

        }

        if(!CollectionUtils.isEmpty(resultList2)) {
            /*dropsList = resultList.stream().map(a-> this.convertDrops(a))
                    .collect(Collectors.toList());*/
            dropsList = this.convertDocs2(resultList2);
            // List<DocsVO> finallist = sortedDocs(dropsList);
            return dropsList;
           // return resultList2;
        }
        else {

            List<OpenDocsVO> emptyDocs = new ArrayList<>();
            dropsList = emptyDocs;
        }
        return dropsList;
    }



    private String convertToString(Object value) {
        if(Objects.nonNull(value)) return value.toString();
        return null;
    }


    private List<OpenDocsVO> convertDocs2(List<Map<String, Object>> list) {
        Map<String, OpenDocsVO> dropsMap = new HashMap<>();
        for(Map<String, Object> map: list) {
            String docNum = this.convertToString(map.get("DOCNUM"));
            OpenDocsVO dropsVO = null;
            if(Objects.nonNull(dropsMap.get(docNum))) {
                dropsVO = dropsMap.get(docNum);
            }else {
                dropsVO = new OpenDocsVO();
            }
            dropsVO.setDocnum(docNum);

            this.convertDocs2(map, dropsVO);
            dropsMap.put(docNum, dropsVO);
        }
        return new ArrayList<>(dropsMap.values());
    }


    private OpenDocsVO convertDocs2(Map<String, Object> drops, OpenDocsVO dropsVO) {

        dropsVO.setDoctype(this.convertToString(drops.get("DOCTYPE")));
        dropsVO.setDocnum(this.convertToString(drops.get("DOCNUM")));
        dropsVO.setRouteStatus(this.convertToString(drops.get("ROUTESTATUS")));
        dropsVO.setDocdate(this.convertToString(drops.get("DOCDATE")));
        dropsVO.setDlvystatus(this.convertToString(drops.get("DLVYSTATUS")));
        dropsVO.setDrivercode(this.convertToString(drops.get("DRIVERCODE")));
        dropsVO.setVehicleCode(this.convertToString(drops.get("VEHICLECODE")));
        dropsVO.setBpcode(this.convertToString(drops.get("BPCODE")));
        dropsVO.setBpname(this.convertToString(drops.get("BPNAME")));
        dropsVO.setAdrescode(this.convertToString(drops.get("ADRESCODE")));
        dropsVO.setAdresname(this.convertToString(drops.get("ADRESNAME")));
        dropsVO.setSite(this.convertToString(drops.get("SITE")));
        dropsVO.setTripno(this.convertToString(drops.get("TRIPNO")));
        dropsVO.setVrcode(this.convertToString(drops.get("VRCODE")));
        dropsVO.setSeq(this.convertToString(drops.get("SEQ")));
        if(Objects.isNull(dropsVO.getProducts())) {
            List<ProductVO> productVOS = new ArrayList<>();
            dropsVO.setProducts(productVOS);
        }
        dropsVO.getProducts().add(getProductVO(drops));

        return dropsVO;
    }


    private ProductVO getProductVO(Map<String, Object> map) {
        ProductVO productVO = new ProductVO();
        productVO.setProductCode(this.convertToString(map.get("PRODUCTCODE")));
        productVO.setDocLineNum(this.convertToString(map.get("DOCLINENO")));
        productVO.setProductName(this.convertToString(map.get("PRODUCTNAME")));
        productVO.setProductCateg(this.convertToString(map.get("PRODUCTCATEG")));
        productVO.setQuantity(this.convertToString(map.get("QUANTITY")));
        if(null != productVO.getQuantity() && productVO.getQuantity().length() > 4) {
            log.info("prod code");
            log.info(productVO.getProductCode());
            log.info("prod line number");
            log.info(productVO.getDocLineNum());
            log.info("prod info");
            log.info(productVO.getQuantity());
            String quant = productVO.getQuantity().substring(0, productVO.getQuantity().indexOf("."));
            productVO.setQuantity(quant);
        }
        productVO.setUom(this.convertToString(map.get("UOM")));
        return productVO;
    }




    public List<OpenDocsRoutesVO> getTripsVOwithRangeofOpenDocs(List<String> site, Date sdate, Date edate) {
        List<OpenDocsRoutes> tripsList = null;
        List<OpenDocsRoutesVO> openRoutes = new ArrayList<>();
        if(!StringUtils.isEmpty(site)) {
            tripsList = openDocsRoutesRepository.getOpenDocsRouteBySiteAndDocdateRange(site,sdate, edate);
        }
        if(!CollectionUtils.isEmpty(tripsList)) {
            return tripsList.stream().map(a -> getTripVO(a)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }


    private OpenDocsRoutesVO getTripVO(OpenDocsRoutes trip) {
        OpenDocsRoutesVO tripVO = new OpenDocsRoutesVO();
        BeanUtils.copyProperties(trip, tripVO);
        tripVO.setTripCode(trip.getTripCode());
        try {
            tripVO.setTotalObject(mapper.readValue(trip.getTotalObject(), Object.class));

        }catch (Exception e) {
            e.printStackTrace();
        }
        return tripVO;
    }

}
