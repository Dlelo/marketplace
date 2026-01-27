package com.example.marketplace.controllers;

import com.example.marketplace.dto.ContractDTO;
import com.example.marketplace.mapper.ContractMapper;
import com.example.marketplace.model.Contract;
import com.example.marketplace.service.ContractService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;
    private final ContractMapper contractMapper;

    @PostMapping
    public ResponseEntity<ContractDTO> createContract(@Valid @RequestBody ContractDTO contractDTO) {
        Contract contract = contractService.hireHouseHelp(
                contractDTO.getHouseHelpId(),
                contractDTO.getHomeOwnerId(),
                contractDTO
        );
        return new ResponseEntity<>(contractMapper.toDTO(contract), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDTO> getContractById(@PathVariable Long id) {
        Contract contract = contractService.getContractById(id);
        return ResponseEntity.ok(contractMapper.toDTO(contract));
    }

    @GetMapping("/househelp/{houseHelpId}")
    public ResponseEntity<List<ContractDTO>> getContractsByHouseHelp(@PathVariable Long houseHelpId) {
        List<Contract> contracts = contractService.getContractsByHouseHelp(houseHelpId);
        List<ContractDTO> dtos = contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/househelp/{houseHelpId}/active")
    public ResponseEntity<ContractDTO> getActiveContractByHouseHelp(@PathVariable Long houseHelpId) {
        Contract contract = contractService.getActiveContractByHouseHelp(houseHelpId);
        return ResponseEntity.ok(contractMapper.toDTO(contract));
    }

    @GetMapping("/homeowner/{homeOwnerId}")
    public ResponseEntity<List<ContractDTO>> getContractsByHomeOwner(@PathVariable Long homeOwnerId) {
        List<Contract> contracts = contractService.getContractsByHomeOwner(homeOwnerId);
        List<ContractDTO> dtos = contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/homeowner/{homeOwnerId}/active")
    public ResponseEntity<List<ContractDTO>> getActiveContractsByHomeOwner(@PathVariable Long homeOwnerId) {
        List<Contract> contracts = contractService.getActiveContractsByHomeOwner(homeOwnerId);
        List<ContractDTO> dtos = contracts.stream()
                .map(contractMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractDTO> updateContract(
            @PathVariable Long id,
            @Valid @RequestBody ContractDTO contractDTO) {
        Contract contract = contractService.updateContract(id, contractDTO);
        return ResponseEntity.ok(contractMapper.toDTO(contract));
    }

    @PatchMapping("/{id}/terminate")
    public ResponseEntity<ContractDTO> terminateContract(@PathVariable Long id) {
        Contract contract = contractService.terminateContract(id);
        return ResponseEntity.ok(contractMapper.toDTO(contract));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ContractDTO> completeContract(@PathVariable Long id) {
        Contract contract = contractService.completeContract(id);
        return ResponseEntity.ok(contractMapper.toDTO(contract));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.deleteContract(id);
        return ResponseEntity.noContent().build();
    }
}
