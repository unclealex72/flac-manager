
package com.amazon.webservices;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import com.amazon.webservices.awsecommerceservice._2006_11_14.BrowseNodeLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CartAddResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CartClearResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CartCreateResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CartGetResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CartModifyResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CustomerContentLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.CustomerContentSearchResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.HelpResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ListLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.ListSearchResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.MultiOperationResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.SellerListingLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.SellerListingSearchResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.SellerLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.SimilarityLookupResponse;
import com.amazon.webservices.awsecommerceservice._2006_11_14.TransactionLookupResponse;

@WebService(name = "AWSECommerceServicePortType", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface AWSECommerceServicePortType {


    @WebMethod(operationName = "ItemSearch", action = "http://soap.amazon.com")
    @WebResult(name = "ItemSearchResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public ItemSearchResponse itemSearch(
        @WebParam(name = "ItemSearch", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.ItemSearch ItemSearch);

    @WebMethod(operationName = "CustomerContentSearch", action = "http://soap.amazon.com")
    @WebResult(name = "CustomerContentSearchResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CustomerContentSearchResponse customerContentSearch(
        @WebParam(name = "CustomerContentSearch", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CustomerContentSearch CustomerContentSearch);

    @WebMethod(operationName = "Help", action = "http://soap.amazon.com")
    @WebResult(name = "HelpResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public HelpResponse help(
        @WebParam(name = "Help", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.Help Help);

    @WebMethod(operationName = "CartAdd", action = "http://soap.amazon.com")
    @WebResult(name = "CartAddResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CartAddResponse cartAdd(
        @WebParam(name = "CartAdd", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CartAdd CartAdd);

    @WebMethod(operationName = "ItemLookup", action = "http://soap.amazon.com")
    @WebResult(name = "ItemLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public ItemLookupResponse itemLookup(
        @WebParam(name = "ItemLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.ItemLookup ItemLookup);

    @WebMethod(operationName = "CartGet", action = "http://soap.amazon.com")
    @WebResult(name = "CartGetResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CartGetResponse cartGet(
        @WebParam(name = "CartGet", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CartGet CartGet);

    @WebMethod(operationName = "CartModify", action = "http://soap.amazon.com")
    @WebResult(name = "CartModifyResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CartModifyResponse cartModify(
        @WebParam(name = "CartModify", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CartModify CartModify);

    @WebMethod(operationName = "CartClear", action = "http://soap.amazon.com")
    @WebResult(name = "CartClearResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CartClearResponse cartClear(
        @WebParam(name = "CartClear", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CartClear CartClear);

    @WebMethod(operationName = "SimilarityLookup", action = "http://soap.amazon.com")
    @WebResult(name = "SimilarityLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public SimilarityLookupResponse similarityLookup(
        @WebParam(name = "SimilarityLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.SimilarityLookup SimilarityLookup);

    @WebMethod(operationName = "SellerListingLookup", action = "http://soap.amazon.com")
    @WebResult(name = "SellerListingLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public SellerListingLookupResponse sellerListingLookup(
        @WebParam(name = "SellerListingLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.SellerListingLookup SellerListingLookup);

    @WebMethod(operationName = "SellerLookup", action = "http://soap.amazon.com")
    @WebResult(name = "SellerLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public SellerLookupResponse sellerLookup(
        @WebParam(name = "SellerLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.SellerLookup SellerLookup);

    @WebMethod(operationName = "BrowseNodeLookup", action = "http://soap.amazon.com")
    @WebResult(name = "BrowseNodeLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public BrowseNodeLookupResponse browseNodeLookup(
        @WebParam(name = "BrowseNodeLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.BrowseNodeLookup BrowseNodeLookup);

    @WebMethod(operationName = "TransactionLookup", action = "http://soap.amazon.com")
    @WebResult(name = "TransactionLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public TransactionLookupResponse transactionLookup(
        @WebParam(name = "TransactionLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.TransactionLookup TransactionLookup);

    @WebMethod(operationName = "SellerListingSearch", action = "http://soap.amazon.com")
    @WebResult(name = "SellerListingSearchResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public SellerListingSearchResponse sellerListingSearch(
        @WebParam(name = "SellerListingSearch", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.SellerListingSearch SellerListingSearch);

    @WebMethod(operationName = "ListSearch", action = "http://soap.amazon.com")
    @WebResult(name = "ListSearchResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public ListSearchResponse listSearch(
        @WebParam(name = "ListSearch", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.ListSearch ListSearch);

    @WebMethod(operationName = "CustomerContentLookup", action = "http://soap.amazon.com")
    @WebResult(name = "CustomerContentLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CustomerContentLookupResponse customerContentLookup(
        @WebParam(name = "CustomerContentLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CustomerContentLookup CustomerContentLookup);

    @WebMethod(operationName = "ListLookup", action = "http://soap.amazon.com")
    @WebResult(name = "ListLookupResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public ListLookupResponse listLookup(
        @WebParam(name = "ListLookup", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.ListLookup ListLookup);

    @WebMethod(operationName = "MultiOperation", action = "http://soap.amazon.com")
    @WebResult(name = "MultiOperationResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public MultiOperationResponse multiOperation(
        @WebParam(name = "MultiOperation", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.MultiOperation MultiOperation);

    @WebMethod(operationName = "CartCreate", action = "http://soap.amazon.com")
    @WebResult(name = "CartCreateResponse", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
    public CartCreateResponse cartCreate(
        @WebParam(name = "CartCreate", targetNamespace = "http://webservices.amazon.com/AWSECommerceService/2006-11-14")
        com.amazon.webservices.awsecommerceservice._2006_11_14.CartCreate CartCreate);

}
