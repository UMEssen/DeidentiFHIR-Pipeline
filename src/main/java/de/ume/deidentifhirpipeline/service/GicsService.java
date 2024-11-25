package de.ume.deidentifhirpipeline.service;

import de.ume.deidentifhirpipeline.config.service.GicsServiceConfig;
import de.ume.deidentifhirpipeline.service.wsdl.gics.*;
import jakarta.xml.ws.BindingProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.*;

public class GicsService {

  private final GicsServiceConfig config;

  private GICSService gicsService;

  public GicsService(GicsServiceConfig config)
      throws MalformedURLException, URISyntaxException {
    this.config = config;

    URL gicsServiceWsdlUrl = new URI(this.config.getMainWsdlUri()).toURL();
    GICSServiceImplService gicsServiceImplService = new GICSServiceImplService(gicsServiceWsdlUrl);
    gicsService = gicsServiceImplService.getGicsServicePort();
    BindingProvider gicsServiceBindingProvider = (BindingProvider) gicsService;
    gicsServiceBindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, gicsServiceWsdlUrl.toString());

  }

  public List<String> getIdsWithAcceptedPolicies(String domain, List<String> policies) throws Exception {
    List<SignerIdDTO> ids = this.getAllConsentsForDomain(domain);
    List<String> patientIds = new ArrayList<>();
    for (SignerIdDTO id : ids) {
      if (this.policiesAccepted(domain, id, policies)) {
        patientIds.add(id.getId());
      }
    }
    return patientIds;
  }

  public List<SignerIdDTO> getAllConsentsForDomain(String domain) throws Exception {
    List<SignerIdDTO> signerIds = gicsService.getAllConsentsForDomain(domain).getConsents().stream()
        .map(c -> c.getKey() != null ? c.getKey().getSignerIds().getFirst() : null)
        .filter(Objects::nonNull)
        .distinct()
        .toList();
    return removeDuplicates(signerIds);
  }

  private boolean policiesAccepted(String domain, SignerIdDTO id, List<String> policies) throws Exception {
    for (String policy : policies) {
      List<SignedPolicyDTO> allPolicies =
          gicsService.getPolicyStatesForPolicyNameAndSignerIds(domain, policy, List.of(id), false)
              .getSignedPolicies();
      if (!this.getNewestPolicy(allPolicies).getStatus().equals(ConsentStatus.ACCEPTED)) {
        return false;
      }
    }
    return true;
  }

  private SignedPolicyDTO getNewestPolicy(List<SignedPolicyDTO> policies) {
    SignedPolicyDTO returnPolicy = policies.getFirst();
    for (SignedPolicyDTO policy : policies) {
      if (returnPolicy == null)
        returnPolicy = policy;
      if (policy.getConsentKey().getConsentDate().compare(returnPolicy.getConsentKey().getConsentDate()) > 0) {
        returnPolicy = policy;
      }
    }
    return returnPolicy;
  }

  private static List<SignerIdDTO> removeDuplicates(List<SignerIdDTO> ids) {
    Map<String, SignerIdDTO> filterDuplicates = new HashMap<>();
    for (SignerIdDTO id : ids) {
      filterDuplicates.put(id.getId(), id);
    }
    return filterDuplicates.values().stream().toList();
  }

}
