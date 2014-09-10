/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.umm.radonc.ca_dash.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.xhtmlrenderer.pdf.ITextRenderer;



/**
 *
 * @author mmcgrath
 */
@ManagedBean
public class PdfTest {
    
    private StreamedContent file;
     
    public PdfTest() {        
        InputStream stream = new ByteArrayInputStream(this.getPdfStream());
        file = new DefaultStreamedContent(stream, "application/pdf", "test.pdf");
    }
 
    public StreamedContent getFile() {
        return file;
    }
    
      private byte[] getPdfStream() {
      byte[] pdf = null;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      String html = "<!DOCTYPE HTML><html><body><img src='images/um-pms.png' /><table><tr><td>Table</td><td>Table 2</td></tr></table></body></html>";
      try {
              ITextRenderer itextRenderer = new ITextRenderer();
              itextRenderer.setDocumentFromString(html);
              itextRenderer.layout();
              itextRenderer.createPDF(baos);
              pdf = baos.toByteArray();
      }
      catch (Exception e) {
       e.printStackTrace();
      }
      return pdf;
    }
    
}
