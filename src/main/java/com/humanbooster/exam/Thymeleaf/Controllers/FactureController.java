package com.humanbooster.exam.Thymeleaf.Controllers;

import com.humanbooster.exam.Thymeleaf.Models.Facture;
import com.humanbooster.exam.Thymeleaf.Repository.FactureRepository;
import com.humanbooster.exam.Thymeleaf.Services.PdfService;
import com.itextpdf.text.DocumentException;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;


@Controller
public class FactureController {

    @Autowired
    PdfService pdfService;

    @Autowired
    FactureRepository factureRepository;

    @RequestMapping("")
    public ModelAndView home(){
        ModelAndView mv = new ModelAndView("home");

        List<Facture> factures = factureRepository.findAll();

        mv.addObject("factures", factures);

        return  mv;
    }

    @RequestMapping("detail/{facture}")
    public ModelAndView detail(@PathVariable(required = false) Facture facture){

        if(facture == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture inexistante");
        }


        GregorianCalendar dateLimite = (GregorianCalendar) facture.getDateFacture().clone();



        dateLimite.add(GregorianCalendar.DAY_OF_YEAR, 90);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateLimitePaiement = dateFormat.format(dateLimite.getTime());

        ModelAndView mv = new ModelAndView("detail");
        mv.addObject("facture", facture);
        mv.addObject("dateLimitePaiement", dateLimitePaiement);


        return  mv;
    }

    @RequestMapping("pdf/{facture}")
    public void pdf(@PathVariable(required = false) Facture facture, HttpServletResponse httpServletResponse) throws DocumentException, IOException {

        if(facture == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Facture inexistante");
        }

        BigDecimal totalHT = facture.getLignes().stream()
                .map(ligne -> ligne.getQuantity().multiply(ligne.getPrixHt()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTTC = facture.getLignes().stream()
                .map(ligne -> ligne.getQuantity().multiply(ligne.getPrixHt())
                        .multiply(BigDecimal.valueOf(1 + ligne.getTva().getTaux())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        this.pdfService.generatePdfFromHtml(facture, totalHT, totalTTC);

        InputStream inputStream = new FileInputStream(
                new File("src/main/resources/static/pdf/facture.pdf")
        );

        IOUtils.copy(inputStream, httpServletResponse.getOutputStream());

        httpServletResponse.setContentType("application/octet-stream");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+facture.getLibelle()+".pdf";

        httpServletResponse.setHeader(headerKey, headerValue);
        httpServletResponse.flushBuffer();
    }


}
