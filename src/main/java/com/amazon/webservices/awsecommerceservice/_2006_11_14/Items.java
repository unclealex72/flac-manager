
package com.amazon.webservices.awsecommerceservice._2006_11_14;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
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
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2006-11-14}Request" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2006-11-14}CorrectedQuery" minOccurs="0"/>
 *         &lt;element name="TotalResults" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element name="TotalPages" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2006-11-14}SearchResultsMap" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2006-11-14}Item" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://webservices.amazon.com/AWSECommerceService/2006-11-14}SearchBinSets" minOccurs="0"/>
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
    "request",
    "correctedQuery",
    "totalResults",
    "totalPages",
    "searchResultsMap",
    "item",
    "searchBinSets"
})
@XmlRootElement(name = "Items")
public class Items {

    @XmlElement(name = "Request")
    protected Request request;
    @XmlElement(name = "CorrectedQuery")
    protected CorrectedQuery correctedQuery;
    @XmlElement(name = "TotalResults")
    protected BigInteger totalResults;
    @XmlElement(name = "TotalPages")
    protected BigInteger totalPages;
    @XmlElement(name = "SearchResultsMap")
    protected SearchResultsMap searchResultsMap;
    @XmlElement(name = "Item", required = true)
    protected List<Item> item;
    @XmlElement(name = "SearchBinSets")
    protected SearchBinSets searchBinSets;

    /**
     * Gets the value of the request property.
     * 
     * @return
     *     possible object is
     *     {@link Request }
     *     
     */
    public Request getRequest() {
        return request;
    }

    /**
     * Sets the value of the request property.
     * 
     * @param value
     *     allowed object is
     *     {@link Request }
     *     
     */
    public void setRequest(Request value) {
        this.request = value;
    }

    /**
     * Gets the value of the correctedQuery property.
     * 
     * @return
     *     possible object is
     *     {@link CorrectedQuery }
     *     
     */
    public CorrectedQuery getCorrectedQuery() {
        return correctedQuery;
    }

    /**
     * Sets the value of the correctedQuery property.
     * 
     * @param value
     *     allowed object is
     *     {@link CorrectedQuery }
     *     
     */
    public void setCorrectedQuery(CorrectedQuery value) {
        this.correctedQuery = value;
    }

    /**
     * Gets the value of the totalResults property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalResults() {
        return totalResults;
    }

    /**
     * Sets the value of the totalResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalResults(BigInteger value) {
        this.totalResults = value;
    }

    /**
     * Gets the value of the totalPages property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getTotalPages() {
        return totalPages;
    }

    /**
     * Sets the value of the totalPages property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setTotalPages(BigInteger value) {
        this.totalPages = value;
    }

    /**
     * Gets the value of the searchResultsMap property.
     * 
     * @return
     *     possible object is
     *     {@link SearchResultsMap }
     *     
     */
    public SearchResultsMap getSearchResultsMap() {
        return searchResultsMap;
    }

    /**
     * Sets the value of the searchResultsMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchResultsMap }
     *     
     */
    public void setSearchResultsMap(SearchResultsMap value) {
        this.searchResultsMap = value;
    }

    /**
     * Gets the value of the item property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Item }
     * 
     * 
     */
    public List<Item> getItem() {
        if (item == null) {
            item = new ArrayList<Item>();
        }
        return this.item;
    }

    /**
     * Gets the value of the searchBinSets property.
     * 
     * @return
     *     possible object is
     *     {@link SearchBinSets }
     *     
     */
    public SearchBinSets getSearchBinSets() {
        return searchBinSets;
    }

    /**
     * Sets the value of the searchBinSets property.
     * 
     * @param value
     *     allowed object is
     *     {@link SearchBinSets }
     *     
     */
    public void setSearchBinSets(SearchBinSets value) {
        this.searchBinSets = value;
    }

}
