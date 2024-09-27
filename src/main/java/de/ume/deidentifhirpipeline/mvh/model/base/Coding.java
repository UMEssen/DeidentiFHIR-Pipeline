package de.ume.deidentifhirpipeline.mvh.model.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coding {
  private String code;
  private String display;
  private String system;
  private String version;

  public Coding(String code) {
    this.code = code;
  }

  public Coding(String code, String display) {
    this.code = code;
    this.display = display;
  }

  public Coding(String code, String display, String system) {
    this.code = code;
    this.display = display;
    this.system = system;
  }

  public static class Gender {
    public static Coding male() {
      Coding coding = new Coding();
      coding.setCode("male");
      coding.setSystem("http://hl7.org/fhir/administrative-gender");
      coding.setDisplay("Male");
      return coding;
    }

    public static Coding female() {
      Coding coding = new Coding();
      coding.setCode("female");
      coding.setSystem("http://hl7.org/fhir/administrative-gender");
      coding.setDisplay("Female");
      return coding;
    }

    public static Coding other() {
      Coding coding = new Coding();
      coding.setCode("other");
      coding.setSystem("http://hl7.org/fhir/administrative-gender");
      coding.setDisplay("Other");
      return coding;
    }

    public static Coding unknown() {
      Coding coding = new Coding();
      coding.setCode("unknown");
      coding.setSystem("http://hl7.org/fhir/administrative-gender");
      coding.setDisplay("Unknown");
      return coding;
    }
  } // class Gender

  public static class ACMG {
    public static final Coding PVS1 = new Coding("PVS1");
    public static final Coding PS1 = new Coding("PS1");
    public static final Coding PS2 = new Coding("PS2");
    public static final Coding PS3 = new Coding("PS3");
    public static final Coding PS4 = new Coding("PS4");
    public static final Coding PM1 = new Coding("PM1");
    public static final Coding PM2 = new Coding("PM2");
    public static final Coding PM3 = new Coding("PM3");
    public static final Coding PM4 = new Coding("PM4");
    public static final Coding PM5 = new Coding("PM5");
    public static final Coding PM6 = new Coding("PM6");
    public static final Coding PP1 = new Coding("PP1");
    public static final Coding PP2 = new Coding("PP2");
    public static final Coding PP3 = new Coding("PP3");
    public static final Coding PP4 = new Coding("PP4");
    public static final Coding PP5 = new Coding("PP5");
    public static final Coding BA1 = new Coding("BA1");
    public static final Coding BS1 = new Coding("BS1");
    public static final Coding BS2 = new Coding("BS2");
    public static final Coding BS3 = new Coding("BS3");
    public static final Coding BS4 = new Coding("BS4");


    public static class Modifier {
      public static Coding pvs() {
        return new Coding("pvs", "very strong pathogenic");
      }
    } // class Modifier
  } // class ACMG

  public static class Diagnosis {
    public static Coding unsolved() {
      return new Coding("unsolved", "Keine genetische Diagnosestellung");
    }
    public static Coding unclear() {
      return new Coding("unclear", "Genetische Verdachtsdiagnose");
    }
    public static Coding solved() {
      return new Coding("solved", "Genetische Diagnose gesichert");
    }
    public static Coding partiallySolved() {
      return new Coding("partially-solved", "Klinischer Phänotyp nur partiell gelöst");
    }
  } // class Diagnosis

  public static class GMFCS {
    public static final Coding levelI     = new Coding("I", "Level I");
    public static final Coding levelII    = new Coding("II", "Level II");
    public static final Coding levelIII   = new Coding("III", "Level III");
    public static final Coding levelIV    = new Coding("IV", "Level IV");
    public static final Coding levelV     = new Coding("V", "Level V");
  } // class GMFCS

  public static class HPOStatus {
    public static final Coding improved = new Coding("improved", "Verbessert");
    public static final Coding worsened = new Coding("worsened", "Verschlechtert");
    public static final Coding resolved = new Coding("resolved", "Weggefallen");
  } // class HPOStatus

  public static class Variant {
    public static class Zygosity {
      public static final Coding heterozygous   = new Coding("heterozygous", "Heterozygous");
      public static final Coding homozygous     = new Coding("homozygous", "Homozygous");
      public static final Coding compHet        = new Coding("comp-het", "Compound heterozygous");
      public static final Coding hemi           = new Coding("hemi", "Hemizygous");
      public static final Coding homoplasmic    = new Coding("homoplasmic", "Homoplasmic");
      public static final Coding heteroplasmic  = new Coding("heteroplasmic", "Heteroplasmic");
    } // class Zygosity
  } // class Variant


}
