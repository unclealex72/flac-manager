package uk.co.unclealex.music.albumcover.service;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.ws.Holder;

import com.amazon.webservices.awsecommerceservice.AWSECommerceService;
import com.amazon.webservices.awsecommerceservice.AWSECommerceServicePortType;
import com.amazon.webservices.awsecommerceservice.BrowseNodeLookup;
import com.amazon.webservices.awsecommerceservice.BrowseNodeLookupRequest;
import com.amazon.webservices.awsecommerceservice.BrowseNodeLookupResponse;
import com.amazon.webservices.awsecommerceservice.BrowseNodes;
import com.amazon.webservices.awsecommerceservice.Cart;
import com.amazon.webservices.awsecommerceservice.CartAdd;
import com.amazon.webservices.awsecommerceservice.CartAddRequest;
import com.amazon.webservices.awsecommerceservice.CartAddResponse;
import com.amazon.webservices.awsecommerceservice.CartClear;
import com.amazon.webservices.awsecommerceservice.CartClearRequest;
import com.amazon.webservices.awsecommerceservice.CartClearResponse;
import com.amazon.webservices.awsecommerceservice.CartCreate;
import com.amazon.webservices.awsecommerceservice.CartCreateRequest;
import com.amazon.webservices.awsecommerceservice.CartCreateResponse;
import com.amazon.webservices.awsecommerceservice.CartGet;
import com.amazon.webservices.awsecommerceservice.CartGetRequest;
import com.amazon.webservices.awsecommerceservice.CartGetResponse;
import com.amazon.webservices.awsecommerceservice.CartModify;
import com.amazon.webservices.awsecommerceservice.CartModifyRequest;
import com.amazon.webservices.awsecommerceservice.CartModifyResponse;
import com.amazon.webservices.awsecommerceservice.CustomerContentLookup;
import com.amazon.webservices.awsecommerceservice.CustomerContentLookupRequest;
import com.amazon.webservices.awsecommerceservice.CustomerContentLookupResponse;
import com.amazon.webservices.awsecommerceservice.CustomerContentSearch;
import com.amazon.webservices.awsecommerceservice.CustomerContentSearchRequest;
import com.amazon.webservices.awsecommerceservice.CustomerContentSearchResponse;
import com.amazon.webservices.awsecommerceservice.Customers;
import com.amazon.webservices.awsecommerceservice.Help;
import com.amazon.webservices.awsecommerceservice.HelpRequest;
import com.amazon.webservices.awsecommerceservice.HelpResponse;
import com.amazon.webservices.awsecommerceservice.Information;
import com.amazon.webservices.awsecommerceservice.ItemLookup;
import com.amazon.webservices.awsecommerceservice.ItemLookupRequest;
import com.amazon.webservices.awsecommerceservice.ItemLookupResponse;
import com.amazon.webservices.awsecommerceservice.ItemSearch;
import com.amazon.webservices.awsecommerceservice.ItemSearchRequest;
import com.amazon.webservices.awsecommerceservice.ItemSearchResponse;
import com.amazon.webservices.awsecommerceservice.Items;
import com.amazon.webservices.awsecommerceservice.ListLookup;
import com.amazon.webservices.awsecommerceservice.ListLookupRequest;
import com.amazon.webservices.awsecommerceservice.ListLookupResponse;
import com.amazon.webservices.awsecommerceservice.ListSearch;
import com.amazon.webservices.awsecommerceservice.ListSearchRequest;
import com.amazon.webservices.awsecommerceservice.ListSearchResponse;
import com.amazon.webservices.awsecommerceservice.Lists;
import com.amazon.webservices.awsecommerceservice.OperationRequest;
import com.amazon.webservices.awsecommerceservice.SellerListingLookup;
import com.amazon.webservices.awsecommerceservice.SellerListingLookupRequest;
import com.amazon.webservices.awsecommerceservice.SellerListingLookupResponse;
import com.amazon.webservices.awsecommerceservice.SellerListingSearch;
import com.amazon.webservices.awsecommerceservice.SellerListingSearchRequest;
import com.amazon.webservices.awsecommerceservice.SellerListingSearchResponse;
import com.amazon.webservices.awsecommerceservice.SellerListings;
import com.amazon.webservices.awsecommerceservice.SellerLookup;
import com.amazon.webservices.awsecommerceservice.SellerLookupRequest;
import com.amazon.webservices.awsecommerceservice.SellerLookupResponse;
import com.amazon.webservices.awsecommerceservice.Sellers;
import com.amazon.webservices.awsecommerceservice.SimilarityLookup;
import com.amazon.webservices.awsecommerceservice.SimilarityLookupRequest;
import com.amazon.webservices.awsecommerceservice.SimilarityLookupResponse;
import com.amazon.webservices.awsecommerceservice.TagLookup;
import com.amazon.webservices.awsecommerceservice.TagLookupRequest;
import com.amazon.webservices.awsecommerceservice.TagLookupResponse;
import com.amazon.webservices.awsecommerceservice.Tags;
import com.amazon.webservices.awsecommerceservice.TransactionLookup;
import com.amazon.webservices.awsecommerceservice.TransactionLookupRequest;
import com.amazon.webservices.awsecommerceservice.TransactionLookupResponse;
import com.amazon.webservices.awsecommerceservice.Transactions;
import com.amazon.webservices.awsecommerceservice.VehiclePartLookup;
import com.amazon.webservices.awsecommerceservice.VehiclePartLookupRequest;
import com.amazon.webservices.awsecommerceservice.VehiclePartLookupResponse;
import com.amazon.webservices.awsecommerceservice.VehiclePartSearch;
import com.amazon.webservices.awsecommerceservice.VehiclePartSearchRequest;
import com.amazon.webservices.awsecommerceservice.VehiclePartSearchResponse;
import com.amazon.webservices.awsecommerceservice.VehicleParts;
import com.amazon.webservices.awsecommerceservice.VehicleSearch;
import com.amazon.webservices.awsecommerceservice.VehicleSearchRequest;
import com.amazon.webservices.awsecommerceservice.VehicleSearchResponse;
import com.amazon.webservices.awsecommerceservice.VehicleYears;

public class AmazonServiceImpl implements AmazonService {

	private String i_accessKey;
	private String i_subscriberId;
	private AWSECommerceServicePortType i_delegate;
	
	@PostConstruct
	public void initialise() {
		setDelegate(new AWSECommerceService().getAWSECommerceServicePort());
	}
	
	public void browseNodeLookup(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			BrowseNodeLookupRequest shared,
			List<BrowseNodeLookupRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<BrowseNodes>> browseNodes) {
		getDelegate().browseNodeLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, browseNodes);
	}
	public void cartAdd(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, CartAddRequest shared,
			List<CartAddRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Cart>> cart) {
		getDelegate().cartAdd(marketplaceDomain, awsAccessKeyId, subscriptionId,
				associateTag, validate, xmlEscaping, shared, request,
				operationRequest, cart);
	}
	public void cartClear(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, CartClearRequest shared,
			List<CartClearRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Cart>> cart) {
		getDelegate().cartClear(marketplaceDomain, awsAccessKeyId, subscriptionId,
				associateTag, validate, xmlEscaping, shared, request,
				operationRequest, cart);
	}
	public void cartCreate(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, CartCreateRequest shared,
			List<CartCreateRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Cart>> cart) {
		getDelegate().cartCreate(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, cart);
	}
	public void cartGet(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, CartGetRequest shared,
			List<CartGetRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Cart>> cart) {
		getDelegate().cartGet(marketplaceDomain, awsAccessKeyId, subscriptionId,
				associateTag, validate, xmlEscaping, shared, request,
				operationRequest, cart);
	}
	public void cartModify(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, CartModifyRequest shared,
			List<CartModifyRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Cart>> cart) {
		getDelegate().cartModify(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, cart);
	}
	public void customerContentLookup(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			CustomerContentLookupRequest shared,
			List<CustomerContentLookupRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<Customers>> customers) {
		getDelegate().customerContentLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, customers);
	}
	public void customerContentSearch(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			CustomerContentSearchRequest shared,
			List<CustomerContentSearchRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<Customers>> customers) {
		getDelegate().customerContentSearch(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, customers);
	}
	public void help(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			HelpRequest shared, List<HelpRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<Information>> information) {
		getDelegate().help(marketplaceDomain, awsAccessKeyId, subscriptionId,
				associateTag, validate, shared, request, operationRequest,
				information);
	}
	public void itemLookup(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, ItemLookupRequest shared,
			List<ItemLookupRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Items>> items) {
		getDelegate().itemLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, items);
	}
	public void itemSearch(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String xmlEscaping,
			String validate, ItemSearchRequest shared,
			List<ItemSearchRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Items>> items) {
		getDelegate().itemSearch(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, xmlEscaping, validate, shared,
				request, operationRequest, items);
	}
	public void listLookup(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, ListLookupRequest shared,
			List<ListLookupRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Lists>> lists) {
		getDelegate().listLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, lists);
	}
	public void listSearch(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, ListSearchRequest shared,
			List<ListSearchRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Lists>> lists) {
		getDelegate().listSearch(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, lists);
	}
	public void multiOperation(
			Help help,
			ItemSearch itemSearch,
			ItemLookup itemLookup,
			ListSearch listSearch,
			ListLookup listLookup,
			CustomerContentSearch customerContentSearch,
			CustomerContentLookup customerContentLookup,
			SimilarityLookup similarityLookup,
			SellerLookup sellerLookup,
			CartGet cartGet,
			CartAdd cartAdd,
			CartCreate cartCreate,
			CartModify cartModify,
			CartClear cartClear,
			TransactionLookup transactionLookup,
			SellerListingSearch sellerListingSearch,
			SellerListingLookup sellerListingLookup,
			TagLookup tagLookup,
			BrowseNodeLookup browseNodeLookup,
			VehicleSearch vehicleSearch,
			VehiclePartSearch vehiclePartSearch,
			VehiclePartLookup vehiclePartLookup,
			Holder<OperationRequest> operationRequest,
			Holder<HelpResponse> helpResponse,
			Holder<ItemSearchResponse> itemSearchResponse,
			Holder<ItemLookupResponse> itemLookupResponse,
			Holder<ListSearchResponse> listSearchResponse,
			Holder<ListLookupResponse> listLookupResponse,
			Holder<CustomerContentSearchResponse> customerContentSearchResponse,
			Holder<CustomerContentLookupResponse> customerContentLookupResponse,
			Holder<SimilarityLookupResponse> similarityLookupResponse,
			Holder<SellerLookupResponse> sellerLookupResponse,
			Holder<CartGetResponse> cartGetResponse,
			Holder<CartAddResponse> cartAddResponse,
			Holder<CartCreateResponse> cartCreateResponse,
			Holder<CartModifyResponse> cartModifyResponse,
			Holder<CartClearResponse> cartClearResponse,
			Holder<TransactionLookupResponse> transactionLookupResponse,
			Holder<SellerListingSearchResponse> sellerListingSearchResponse,
			Holder<SellerListingLookupResponse> sellerListingLookupResponse,
			Holder<TagLookupResponse> tagLookupResponse,
			Holder<BrowseNodeLookupResponse> browseNodeLookupResponse,
			Holder<VehicleSearchResponse> vehicleSearchResponse,
			Holder<VehiclePartSearchResponse> vehiclePartSearchResponse,
			Holder<VehiclePartLookupResponse> vehiclePartLookupResponse) {
		getDelegate().multiOperation(help, itemSearch, itemLookup, listSearch,
				listLookup, customerContentSearch, customerContentLookup,
				similarityLookup, sellerLookup, cartGet, cartAdd, cartCreate,
				cartModify, cartClear, transactionLookup, sellerListingSearch,
				sellerListingLookup, tagLookup, browseNodeLookup,
				vehicleSearch, vehiclePartSearch, vehiclePartLookup,
				operationRequest, helpResponse, itemSearchResponse,
				itemLookupResponse, listSearchResponse, listLookupResponse,
				customerContentSearchResponse, customerContentLookupResponse,
				similarityLookupResponse, sellerLookupResponse,
				cartGetResponse, cartAddResponse, cartCreateResponse,
				cartModifyResponse, cartClearResponse,
				transactionLookupResponse, sellerListingSearchResponse,
				sellerListingLookupResponse, tagLookupResponse,
				browseNodeLookupResponse, vehicleSearchResponse,
				vehiclePartSearchResponse, vehiclePartLookupResponse);
	}
	public void sellerListingLookup(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			SellerListingLookupRequest shared,
			List<SellerListingLookupRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<SellerListings>> sellerListings) {
		getDelegate().sellerListingLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, sellerListings);
	}
	public void sellerListingSearch(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			SellerListingSearchRequest shared,
			List<SellerListingSearchRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<SellerListings>> sellerListings) {
		getDelegate().sellerListingSearch(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, sellerListings);
	}
	public void sellerLookup(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, SellerLookupRequest shared,
			List<SellerLookupRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<Sellers>> sellers) {
		getDelegate().sellerLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, sellers);
	}
	public void similarityLookup(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			SimilarityLookupRequest shared,
			List<SimilarityLookupRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Items>> items) {
		getDelegate().similarityLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, items);
	}
	public void tagLookup(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, TagLookupRequest shared,
			List<TagLookupRequest> request,
			Holder<OperationRequest> operationRequest, Holder<List<Tags>> tags) {
		getDelegate().tagLookup(marketplaceDomain, awsAccessKeyId, subscriptionId,
				associateTag, validate, xmlEscaping, shared, request,
				operationRequest, tags);
	}
	public void transactionLookup(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			TransactionLookupRequest shared,
			List<TransactionLookupRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<Transactions>> transactions) {
		getDelegate().transactionLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, transactions);
	}
	public void vehiclePartLookup(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			VehiclePartLookupRequest shared,
			List<VehiclePartLookupRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<VehicleParts>> vehicleParts) {
		getDelegate().vehiclePartLookup(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, vehicleParts);
	}
	public void vehiclePartSearch(String marketplaceDomain,
			String awsAccessKeyId, String subscriptionId, String associateTag,
			String validate, String xmlEscaping,
			VehiclePartSearchRequest shared,
			List<VehiclePartSearchRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<VehicleParts>> vehicleParts) {
		getDelegate().vehiclePartSearch(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, vehicleParts);
	}
	public void vehicleSearch(String marketplaceDomain, String awsAccessKeyId,
			String subscriptionId, String associateTag, String validate,
			String xmlEscaping, VehicleSearchRequest shared,
			List<VehicleSearchRequest> request,
			Holder<OperationRequest> operationRequest,
			Holder<List<VehicleYears>> vehicleYears) {
		getDelegate().vehicleSearch(marketplaceDomain, awsAccessKeyId,
				subscriptionId, associateTag, validate, xmlEscaping, shared,
				request, operationRequest, vehicleYears);
	}
	
	public String getAccessKey() {
		return i_accessKey;
	}
	
	public void setAccessKey(String accessKey) {
		i_accessKey = accessKey;
	}
	
	public String getSubscriberId() {
		return i_subscriberId;
	}
	
	public void setSubscriberId(String subscriberId) {
		i_subscriberId = subscriberId;
	}
	
	public AWSECommerceServicePortType getDelegate() {
		return i_delegate;
	}
	public void setDelegate(AWSECommerceServicePortType delegate) {
		i_delegate = delegate;
	}
}
