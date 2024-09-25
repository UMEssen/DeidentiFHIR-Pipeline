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
    public static Coding pvs1() { return new Coding("PVS1"); }
    public static Coding ps1() { return new Coding("PS1"); }
    public static Coding ps2() { return new Coding("PS2"); }
    public static Coding ps3() { return new Coding("PS3"); }
    public static Coding ps4() { return new Coding("PS4"); }
    public static Coding pm1() { return new Coding("PM1"); }
    public static Coding pm2() { return new Coding("PM2"); }
    public static Coding pm3() { return new Coding("PM3"); }
    public static Coding pm4() { return new Coding("PM4"); }
    public static Coding pm5() { return new Coding("PM5"); }
    public static Coding pm6() { return new Coding("PM6"); }
    public static Coding pp1() { return new Coding("PP1"); }
    public static Coding pp2() { return new Coding("PP2"); }
    public static Coding pp3() { return new Coding("PP3"); }
    public static Coding pp4() { return new Coding("PP4"); }
    public static Coding pp5() { return new Coding("PP5"); }
    public static Coding ba1() { return new Coding("BA1"); }
    public static Coding bs1() { return new Coding("BS1"); }
    public static Coding bs2() { return new Coding("BS2"); }
    public static Coding bs3() { return new Coding("BS3"); }
    public static Coding bs4() { return new Coding("BS4"); }


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
    public static Coding levelI() {
      return new Coding("I", "Level I");
    }
    public static Coding levelII() {
      return new Coding("II", "Level II");
    }
    public static Coding levelIII() {
      return new Coding("III", "Level III");
    }
    public static Coding levelIV() {
      return new Coding("IV", "Level IV");
    }
    public static Coding levelV() {
      return new Coding("V", "Level V");
    }
  } // class GMFCS

  public static class HPOStatus {
    public static Coding improved() {
      return new Coding("improved", "Verbessert");
    }
    public static Coding worsened() {
      return new Coding("worsened", "Verschlechtert");
    }
    public static Coding resolved() {
      return new Coding("resolved", "Weggefallen");
    }
  } // class HPOStatus

  public static class Variant {
    public static class Zygosity {
      public static Coding heterozygous() {
        return new Coding("heterozygous", "Heterozygous");
      }

      public static Coding homozygous() {
        return new Coding("homozygous", "Homozygous");
      }

      public static Coding compHet() {
        return new Coding("comp-het", "Compound heterozygous");
      }

      public static Coding hemi() {
        return new Coding("hemi", "Hemizygous");
      }

      public static Coding homoplasmic() {
        return new Coding("homoplasmic", "Homoplasmic");
      }

      public static Coding heteroplasmic() {
        return new Coding("heteroplasmic", "Heteroplasmic");
      }
    } // class Zygosity
  } // class Variant


}
