package ipleiria.dae.project.dtos;

import ipleiria.dae.project.entities.Client;
import ipleiria.dae.project.entities.Occurrence;
import ipleiria.dae.project.entities.Policy;
import ipleiria.dae.project.enumerators.InsuredAssetType;

import javax.persistence.Id;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class PolicyDTO implements Serializable {
    @Id
    private String code;
    private String type;
    private String client_username;
    private List<InsuredAssetType> covers;

    public PolicyDTO() {
    }

    public PolicyDTO(String code, String type, String client_username) {
        this.code = code;
        this.type = type;
        this.client_username = client_username;
        covers = new LinkedList<>();
    }

    public PolicyDTO(String code, String type, String client_username, List<InsuredAssetType> covers) {
        this.code = code;
        this.type = type;
        this.client_username = client_username;
        this.covers = covers;
    }

    public static PolicyDTO from(Policy policy) {
        return new PolicyDTO(
                policy.getCode(),
                policy.getType(),
                policy.getClient().getUsername(),
                policy.getCovers()
        );
    }

    public static List<PolicyDTO> from(List<Policy> policies) {
        return policies.stream().map(PolicyDTO::from).collect(Collectors.toList());
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClient_username() {
        return client_username;
    }

    public void setClient_username(String client_username) {
        this.client_username = client_username;
    }

    public List<InsuredAssetType> getCovers() {
        return covers;
    }

    public void setCovers(List<InsuredAssetType> covers) {
        this.covers = covers;
    }
}
