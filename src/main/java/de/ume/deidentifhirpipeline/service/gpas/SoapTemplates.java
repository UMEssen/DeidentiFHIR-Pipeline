package de.ume.deidentifhirpipeline.service.gpas;

import java.util.List;

public class SoapTemplates {
  private SoapTemplates() {
    throw new IllegalStateException();
  }

  public static String getDomainXmlString(String domainName) {
    String getDomainTemplate =
        """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
               <soapenv:Header/>
               <soapenv:Body>
                  <psn:getDomain>
                     <domainName>%s</domainName>
                  </psn:getDomain>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    return String.format(getDomainTemplate, domainName);
  }

  public static String addDomainXmlString(String name, String label, String checkDigitClass,
      String alphabet, String forceCache, String includePrefixInCheckDigitCalculation,
      String includeSuffixInCheckDigitCalculation, String maxDetectedError, String psnLength,
      String psnsDeletable, String useLastCharAsDelimiterAfterXChars, String comment) {
    String addDomainTemplate =
        """
            <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
               <soapenv:Header/>
               <soapenv:Body>
                  <psn:addDomain>
                     <domainDTO>
                        <!--Optional:-->
                        <name>%s</name>
                        <!--Optional:-->
                        <label>%s</label>
                        <!--Optional:-->
                        <checkDigitClass>%s</checkDigitClass>
                        <!--Optional:-->
                        <alphabet>%s</alphabet>
                        <!--Optional:-->
                        <config>
                           <!--Optional:-->
                           <forceCache>%s</forceCache>
                           <includePrefixInCheckDigitCalculation>%s</includePrefixInCheckDigitCalculation>
                           <includeSuffixInCheckDigitCalculation>%s</includeSuffixInCheckDigitCalculation>
                           <maxDetectedErrors>%s</maxDetectedErrors>
                           <psnLength>%s</psnLength>
                           <psnsDeletable>%s</psnsDeletable>
                           <sendNotificationsWeb>false</sendNotificationsWeb>
                           <useLastCharAsDelimiterAfterXChars>%s</useLastCharAsDelimiterAfterXChars>
                        </config>
                        <!--Optional:-->
                        <comment>%s</comment>
                     </domainDTO>
                  </psn:addDomain>
               </soapenv:Body>
            </soapenv:Envelope>
            """;
    return String.format(addDomainTemplate,
        name,
        label,
        checkDigitClass,
        alphabet,
        forceCache,
        includePrefixInCheckDigitCalculation,
        includeSuffixInCheckDigitCalculation,
        maxDetectedError,
        psnLength,
        psnsDeletable,
        useLastCharAsDelimiterAfterXChars,
        comment);
  }

  public static String getPseudonymForXmlString(String value, String domainName) {
    String getPseudonymForTemplate =
        """
              <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
                <soapenv:Header/>
                <soapenv:Body>
                   <psn:getPseudonymFor>
                      <value>%s</value>
                      <domainName>%s</domainName>
                   </psn:getPseudonymFor>
                </soapenv:Body>
              </soapenv:Envelope>
            """;
    return String.format(getPseudonymForTemplate, value, domainName);
  }

  public static String getGetPseudonymForListXmlString(List<String> values, String domain) {
    String getPseudonymForListTemplate =
        """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <psn:getPseudonymForList>
                         %s
                         <domainName>%s</domainName>
                      </psn:getPseudonymForList>
                   </soapenv:Body>
                </soapenv:Envelope>
            """;
    return String.format(getPseudonymForListTemplate, valuesToXmlString(values), domain);
  }

  public static String getOrCreatePseudonymForListXmlString(List<String> values, String domain) {
    String getOrCreatePseudonymForListTemplate =
        """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <psn:getOrCreatePseudonymForList>
                         %s
                         <domainName>%s</domainName>
                      </psn:getOrCreatePseudonymForList>
                   </soapenv:Body>
                </soapenv:Envelope>
            """;
    return String.format(getOrCreatePseudonymForListTemplate, valuesToXmlString(values), domain);
  }

  public static String deleteEntry(String value, String domainName) {
    String deleteEntryTemplate = """
        <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
           <soapenv:Header/>
           <soapenv:Body>
              <psn:deleteEntry>
                 <value>%s</value>
                 <domainName>%s</domainName>
              </psn:deleteEntry>
           </soapenv:Body>
        </soapenv:Envelope>
        """;
    return String.format(deleteEntryTemplate, value, domainName);
  }

  public static String getInsertValuePseudonymPairXmlString(String value, String pseudonym, String domainName) {
    String insertValuePseudonymPairTemplate =
        """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:psn="http://psn.ttp.ganimed.icmvc.emau.org/">
                   <soapenv:Header/>
                   <soapenv:Body>
                      <psn:insertValuePseudonymPair>
                         <value>%s</value>
                         <pseudonym>%s</pseudonym>
                         <domainName>%s</domainName>
                      </psn:insertValuePseudonymPair>
                   </soapenv:Body>
                </soapenv:Envelope>
            """;
    return String.format(insertValuePseudonymPairTemplate, value, pseudonym, domainName);
  }

  private static String valuesToXmlString(List<String> values) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String value : values) {
      stringBuilder.append("<values>").append(value).append("</values>");
    }
    return stringBuilder.toString();
  }
}
