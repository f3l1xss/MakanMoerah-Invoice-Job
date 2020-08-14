package com.felixstanley.makanmoerahinvoicejob.dao;

import com.felixstanley.makanmoerahinvoicejob.entity.Invoice;
import com.felixstanley.makanmoerahinvoicejob.entity.InvoiceId;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Felix
 */
public interface InvoiceDao extends JpaRepository<Invoice, InvoiceId> {

}
