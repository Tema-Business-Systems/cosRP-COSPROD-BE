package com.transport.tracking.repository;

import com.transport.tracking.model.OpenDocs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;


public interface OpenDocsRepository extends CrudRepository<OpenDocs, String> {

    public List<OpenDocs> findBySite(String site);

    public List<OpenDocs> findBySiteAndDocdate(String site, Date date);




}
