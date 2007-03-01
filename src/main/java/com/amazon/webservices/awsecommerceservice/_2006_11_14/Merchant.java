
package com.amazon.webservices.awsecommerceservice._2006_11_14;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MerchantId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="GlancePage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AverageFeedbackRating" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="TotalFeedback" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="TotalFeedbackPages" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "merchantId",
    "name",
    "glancePage",
    "averageFeedbackRating",
    "totalFeedback",
    "totalFeedbackPages"
})
@XmlRootElement(name = "Merchant")
public class Merchant {

    @XmlElement(name = "MerchantId", required = true)
    protected String merchantId;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "GlancePage")
    protected String glancePage;
    @XmlElement(name = "AverageFeedbackRating")
    protected BigDecimal averageFeedbackRating;
    @XmlElement(name = "TotalFeedback")
    protected BigInteger totalFeedback;
    @XmlElement(name = "TotalFeedbackPages")
    protected BigInteger totalFeedbackPages;

    /**
     * Gets the value of the merchantId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMerchantId() {
        return merchantId;
    }

    /**
     * Sets the value of the merchantId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMerchantId(String value) {
        this.merchantId = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the glancePage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGlancePage() {
        return glancePage;
    }

    /**
     * Sets the value of the glancePage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGlancePage(String value) {
        this.glancePage = value;
    }

    /**
     * Gets the value of the averageFeedbackRating property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAverageFeedbackRating() {
        return averageFeedbackRating;
    }

    /**
     * Sets the value of the averageFeedbackRating property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAverageFeedbackRating(BigDecimal value) {
        this.averageFeedbackRating = value;
    }

    /**
     * Gets the value of the totalFeedback property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalFeedback() {
        return totalFeedback;
    }

    /**
     * Sets the value of the totalFeedback property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalFeedback(BigInteger value) {
        this.totalFeedback = value;
    }

    /**
     * Gets the value of the totalFeedbackPages property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalFeedbackPages() {
        return totalFeedbackPages;
    }

    /**
     * Sets the value of the totalFeedbackPages property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalFeedbackPages(BigInteger value) {
        this.totalFeedbackPages = value;
    }

}
