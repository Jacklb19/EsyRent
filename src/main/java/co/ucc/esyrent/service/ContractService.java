package co.ucc.esyrent.service;

import co.ucc.esyrent.dto.request.CancelContractRequest;
import co.ucc.esyrent.dto.request.CreateContractRequest;
import co.ucc.esyrent.dto.response.ContractResponse;
import java.util.List;

public interface ContractService {

    ContractResponse createContract(CreateContractRequest request);

    ContractResponse getContractById(Long contractId);

    List<ContractResponse> getContractsByTenant(Long tenantId);

    List<ContractResponse> getContractsByProperty(Long propertyId);

    ContractResponse cancelContract(Long contractId, CancelContractRequest request);
}
