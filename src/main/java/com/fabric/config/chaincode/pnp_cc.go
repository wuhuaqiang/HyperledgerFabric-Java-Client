package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"math/rand"
	"strconv"
	"time"

	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// PnpChaincode cc
type PnpChaincode struct {
}

// Borrower asser
type Borrower struct {
	ObjectType     string       `json:"docType"`        //
	IdentityNo     string       `json:"identityNo"`     //
	Name           string       `json:"name"`           //
	Dob            string       `json:"dob"`            //
	Maritialstatus string       `json:"maritialStatus"` //
	RegisterdDate  string       `json:"regDate"`        //
	Contact        ContactPoint `json:"contact"`
}

// ContactPoint
type ContactPoint struct {
	addrLine    string `json:"addrLine"`    //
	city        string `json:"city"`        //
	province    string `json:"province"`    //
	postalCode  string `json:"postalCode"`  //
	countryCode string `json:"countryCode"` //
	telNo       string `json:"telNo"`       //
}

// LoanApplication
type LoanApplication struct {
	ObjectType   string  `json:"docType"`
	BorrowerId   string  `json:"borrowerIde"`  //
	LoanAmount   float64 `json:"loanAmount"`   //
	LoanCurrency string  `json:"loanCurrency"` //
	Status       string  `json:"status"`       //
	AppliedDate  string  `json:"appliedDate"`  //
	ApplId       string  `json:"applId"`       //
}

type Lender struct {
	ObjectType     string       `json:"docType"`        //
	IdentityNo     string       `json:"identityNo"`     //
	Name           string       `json:"name"`           //
	Dob            string       `json:"dob"`            //
	Maritialstatus string       `json:"maritialStatus"` //
	RegisterdDate  string       `json:"regDate"`        //
	Contact        ContactPoint `json:"contact"`
	AccountNo      string       `json:"accountNo"`
	AccountBal     float64      `json:"acountBalance"`
}
type LendingProposal struct {
	ObjectType   string  `json:"docType"`
	LenderId     string  `json:"lenderId"`     //
	CommitAmount float64 `json:"commitAmount"` //
	RemAmount    float64 `json:"remAmount"`    //
	IntRate      float64 `json:"intRate"`      //
	LoanCurrency string  `json:"loanCurrency"` //
	Status       string  `json:"status"`       //
	RegDate      string  `json:"appliedDate"`  //
	ProposalId   string  `json:"proposalId"`   //
}

type LoanContract struct {
	ObjectType        string  `json:"docType"`
	LoanContractId    string  `json:"loanContractId"`
	LoanApplicationNo string  `json:"loaApplId"`
	Borrower          string  `json:"borrowerId"`
	LendingProposalId string  `json:"lendingProposalId"`
	Lender            string  `json:"lenderId"`
	SanctionedAmount  float64 `json:"sanctionedAmount"`
	IntRate           float64 `json:"intRate"`
	RepaymentTerm     int     `json:"repaymentTerm"`
	RegDate           string  `json:"appliedDate"` //
}

//Init
func (t *PnpChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("PnpChaincode is bootstrapping ...")
	return shim.Success(nil)
}

// Invoke
func (t *PnpChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("starting invoke, for - " + function)
	fmt.Printf("%d\n", len(args))
	if function == "init" {
		return t.Init(stub)
	} else if function == "registerBorrower" {
		return t.RegisterBorrower(stub, args)
	} else if function == "submitLoanApplication" {
		return t.SubmitLoanApplication(stub, args)
	} else if function == "queryAssetForParticipant" {
		return t.QueryAssetForParticipant(stub, args)
	} else if function == "registerLender" {
		return t.RegisterLender(stub, args)
	} else if function == "queryAssetData" {
		return t.QueryAssetData(stub, args)
	} else if function == "queryParticipant" {
		return t.QueryParticipant(stub, args)
	} else if function == "submitLendingProposal" {
		return t.SubmitLendingProposal(stub, args)
	} else if function == "registerContract" {
		return t.RegisterContract(stub, args)
	} else if function == "getQueryResult" {
		return t.GetQueryResult(stub, args)
	} else if function == "getHistoryForAsset" {
		return t.GetHistoryForAsset(stub, args)
	}

	//fmt.Println("invoke did not find func: " + function) //error
	return shim.Error("Received unknown function invocation")
}

// Register Contract
func (t *PnpChaincode) RegisterContract(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	src, err := strconv.ParseInt(args[1], 10, 64)
	r := rand.New(rand.NewSource(src))
	loanContractId := args[1] + fmt.Sprint(r.Uint32())
	loanApplId := args[0]
	borrower := args[1]
	lendingProposalId := args[2]
	lenderId := args[3]
	sanctionAmount, err := strconv.ParseFloat(args[4], 64)
	intRate, err := strconv.ParseFloat(args[5], 64)
	repaymentTerm, err := strconv.Atoi(args[6])
	contract := LoanContract{ObjectType: "CONTRACT", LoanContractId: loanContractId,
		LoanApplicationNo: loanApplId, Borrower: borrower, LendingProposalId: lendingProposalId,
		Lender: lenderId, SanctionedAmount: sanctionAmount, IntRate: intRate, RepaymentTerm: repaymentTerm,
		RegDate: ""}
	//time.Now()

	contractJSONBytes, err := json.Marshal(contract)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(loanContractId, contractJSONBytes)
	indexName := "borrower~lender~contractId"
	indexKey, _ := stub.CreateCompositeKey(indexName, []string{borrower, lenderId, loanContractId})
	value := []byte{0x00}
	stub.PutState(indexKey, value)
	fmt.Println("Contract Created")
	jsonResp := "{\"contarctId\":\"" + loanContractId + "\"}"
	return shim.Success([]byte(jsonResp))

}

// RegisterLender
func (t *PnpChaincode) RegisterLender(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	identityNo := args[0]
	name := args[1]
	dob := args[2]
	maritialStatus := args[3]
	accountNo := args[4]
	accountBal, _ := strconv.ParseFloat(args[5], 64)
	countryCode := args[11]
	postalCode := args[6]
	addrLine := args[7]
	city := args[8]
	province := args[9]
	telephoneNo := args[10]

	// check if borrower exits
	borrowerAsBytes, err := stub.GetState(identityNo)
	if err != nil {
		return shim.Error("Failed to get borrower")
	} else if borrowerAsBytes != nil {
		return shim.Error("This borrower already exists")
	}

	objType := "LNDR"
	regDate := ""
	contact := ContactPoint{addrLine: addrLine, city: city, province: province, postalCode: postalCode, countryCode: countryCode, telNo: telephoneNo}
	borrower := Lender{ObjectType: objType, IdentityNo: identityNo, Name: name, Dob: dob, Maritialstatus: maritialStatus, RegisterdDate: regDate, Contact: contact, AccountNo: accountNo, AccountBal: accountBal}
	borrowerJSONasBytes, err := json.Marshal(borrower)
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(identityNo, borrowerJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// RegisterBorrower
func (t *PnpChaincode) RegisterBorrower(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	identityNo := args[0]
	name := args[1]
	dob := args[2]
	maritialStatus := args[3]
	countryCode := args[5]
	postalCode := args[6]
	addrLine := args[7]
	city := args[8]
	province := args[9]
	telephoneNo := args[4]

	// check if borrower exits
	borrowerAsBytes, err := stub.GetState(identityNo)
	if err != nil {
		return shim.Error("Failed to get borrower")
	} else if borrowerAsBytes != nil {
		return shim.Error("This borrower already exists")
	}

	objType := "BRWR"
	regDate := ""
	//time.Now()
	contact := ContactPoint{addrLine: addrLine, city: city, province: province, postalCode: postalCode, countryCode: countryCode, telNo: telephoneNo}
	// add vontact details to borrower <todo>
	borrower := Borrower{ObjectType: objType, IdentityNo: identityNo, Name: name, Dob: dob, Maritialstatus: maritialStatus, RegisterdDate: regDate, Contact: contact}
	borrowerJSONasBytes, err := json.Marshal(borrower)
	if err != nil {
		return shim.Error(err.Error())
	}

	err = stub.PutState(identityNo, borrowerJSONasBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(nil)
}

// SubmitLoanApplication

func (t *PnpChaincode) SubmitLoanApplication(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	loanAmount, err := strconv.ParseFloat(args[1], 64)
	// generates a unique id for application
	src, err := strconv.ParseInt(args[1], 10, 64)
	r := rand.New(rand.NewSource(src))
	applId := args[0] + fmt.Sprint(r.Uint32())
	fmt.Println("::::::::" + args[0] + ":::" + args[1])
	loanAppl := LoanApplication{ObjectType: "LAPL", BorrowerId: args[0], LoanAmount: loanAmount, LoanCurrency: args[2], Status: "APPLIED", ApplId: applId}
	appJSONBytes, err := json.Marshal(loanAppl)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(applId, appJSONBytes)
	indexName := "borrower~applId"
	indexKey, _ := stub.CreateCompositeKey(indexName, []string{args[0], applId})
	value := []byte{0x00}
	stub.PutState(indexKey, value)
	fmt.Println("Application submitted")
	return shim.Success([]byte(applId))
}

func (t *PnpChaincode) SubmitLendingProposal(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	commitAmount, err := strconv.ParseFloat(args[1], 64)
	remAmount, err := strconv.ParseFloat(args[2], 64)
	intRate, err := strconv.ParseFloat(args[3], 64)
	src, err := strconv.ParseInt(args[1], 10, 64)
	r := rand.New(rand.NewSource(src))
	proposalId := args[0] + fmt.Sprint(r.Uint32())
	lendingProposal := LendingProposal{ObjectType: "LEPRPL", LenderId: args[0], CommitAmount: commitAmount, RemAmount: remAmount, IntRate: intRate, LoanCurrency: args[4], Status: "PROPOSED", RegDate: "", ProposalId: proposalId}
	appJSONBytes, err := json.Marshal(lendingProposal)
	if err != nil {
		return shim.Error(err.Error())
	}
	err = stub.PutState(proposalId, appJSONBytes)
	if err != nil {
		return shim.Error(err.Error())
	}
	indexName := "lender~proposalId"
	indexKey, _ := stub.CreateCompositeKey(indexName, []string{args[0], proposalId})
	value := []byte{0x00}
	err = stub.PutState(indexKey, value)
	if err != nil {
		return shim.Error(err.Error())
	}
	fmt.Println("Proposal submitted")
	return shim.Success([]byte(proposalId))
}

func (t *PnpChaincode) QueryAssetForParticipant(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	borrowerId := args[0]

	indexName := "borrower~applId"
	if args[1] == "Lender" {
		indexName = "lender~proposalId"
	} else if args[1] == "CONTRACT" {
		indexName = "borrower~lender~contractId"
	}

	resultItr, err := stub.GetStateByPartialCompositeKey(indexName, []string{borrowerId})
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultItr.Close()
	var i int
	for i = 0; resultItr.HasNext(); i++ {
		responseRange, err := resultItr.Next()
		if err != nil {
			return shim.Error(err.Error())
		}

		objectType, compositeKeyParts, err := stub.SplitCompositeKey(responseRange.Key)
		if err != nil {
			return shim.Error(err.Error())
		}
		returnedBorrowerId := compositeKeyParts[0]
		returnedApplId := compositeKeyParts[1]
		fmt.Printf("- found a application from index:%s borrower:%s application:%s\n", objectType, returnedBorrowerId, returnedApplId)
		responsePayload := fmt.Sprintf("Found %d %s application for %s", i, returnedApplId, returnedBorrowerId)
		fmt.Println("- end find borrowerApplication: " + responsePayload)
		assetData := t.QueryAssetData(stub, []string{returnedApplId})
		return assetData

	}
	return shim.Error("No Application (indexKey) found")
}

// QueryAssetData
func (t *PnpChaincode) QueryAssetData(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	applicationId := args[0]
	fmt.Println("Searching asset for asset id:" + applicationId)
	appDataasBytes, err := stub.GetState(applicationId)
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + applicationId + "\"}"
		return shim.Error(jsonResp)
	} else if appDataasBytes == nil {
		jsonResp := "{\"Error\":\"Application does not exist: " + applicationId + "\"}"
		return shim.Error(jsonResp)
	}

	return shim.Success(appDataasBytes)
}

func (t *PnpChaincode) QueryParticipant(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	identityId := args[0]
	paricipantDataBytes, err := stub.GetState(identityId)
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + identityId + "\"}"
		return shim.Error(jsonResp)
	} else if paricipantDataBytes == nil {
		jsonResp := "{\"Error\":\"Particpant does not exist: " + identityId + "\"}"
		return shim.Error(jsonResp)
	}

	return shim.Success(paricipantDataBytes)

}

// localhost:4000/channels/mychannel/chaincodes/pnp_go1?peer=org1-peer1&fcn=getQueryResult&args=["{\"selector\":{\"docType\":\"LEPRPL\", \"commitAmount\":{\"$gte\":1000}}}"]
func (t *PnpChaincode) GetQueryResult(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	//   0
	// "queryString"
	if len(args) < 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	queryString := args[0]

	queryResults, err := getQueryResultForQueryString(stub, queryString)
	if err != nil {
		return shim.Error(err.Error())
	}
	return shim.Success(queryResults)
}

// =========================================================================================
// getQueryResultForQueryString executes the passed in query string.
// Result set is built and returned as a byte array containing the JSON results.
// =========================================================================================
func getQueryResultForQueryString(stub shim.ChaincodeStubInterface, queryString string) ([]byte, error) {

	fmt.Printf("- getQueryResultForQueryString queryString:\n%s\n", queryString)

	resultsIterator, err := stub.GetQueryResult(queryString)
	if err != nil {
		return nil, err
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryRecords
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return nil, err
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Key\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getQueryResultForQueryString queryResult:\n%s\n", buffer.String())

	return buffer.Bytes(), nil
}

func (t *PnpChaincode) GetHistoryForAsset(stub shim.ChaincodeStubInterface, args []string) pb.Response {

	if len(args) < 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	asset := args[0]

	fmt.Printf("- start getHistoryForAsset: %s\n", asset)

	resultsIterator, err := stub.GetHistoryForKey(asset)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		response, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"TxId\":")
		buffer.WriteString("\"")
		buffer.WriteString(response.TxId)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Value\":")
		if response.IsDelete {
			buffer.WriteString("null")
		} else {
			buffer.WriteString(string(response.Value))
		}

		buffer.WriteString(", \"Timestamp\":")
		buffer.WriteString("\"")
		buffer.WriteString(time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).String())
		buffer.WriteString("\"")

		buffer.WriteString(", \"IsDelete\":")
		buffer.WriteString("\"")
		buffer.WriteString(strconv.FormatBool(response.IsDelete))
		buffer.WriteString("\"")

		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	fmt.Printf("- getHistoryForAsset returning:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

func main() {
	err := shim.Start(new(PnpChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode - %s", err)
	}
}
