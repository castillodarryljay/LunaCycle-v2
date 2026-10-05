package com.example.ai

import com.example.data.model.UserEntity
import com.example.engine.PredictionResult

/**
 * Specialized Clinical Knowledge Domain:
 * Vaginal Microbiome, Discharge, Pelvic Floor, Period Care Products & Heavy Bleeding (PALM-COEIN).
 */
object IntimacyAndVaginalHealthKnowledge {

    fun matchAndRespond(
        query: String,
        user: UserEntity,
        prediction: PredictionResult,
        cycleDayInt: Int,
        citation: String
    ): String? {
        val lower = query.lowercase().trim()

        // 1. Vaginal Discharge, Odor, Yeast Infection, BV & Microbiome
        if (containsAny(lower, "discharge", "smell", "odor", "yeast", "bv", "bacterial vaginosis", "itch", "burning", "cottage cheese", "white discharge", "yellow discharge", "green discharge")) {
            return """
                ### 🩺 Clinical Gynecology: Vaginal Microbiome & Discharge Differential
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 Differential Diagnostic Guide:
                1. **Physiological Leukorrhea (Normal):**
                   • **Appearance:** Clear, stretchy, or thin milky-white. No foul odor. Varies across the cycle (copious, slippery, and egg-white during the ovulatory fertile window; thick and tacky in the luteal phase).
                   • **Normal pH:** 3.8 to 4.5 maintained by lactic-acid-producing *Lactobacillus crispatus*.
                2. **Vulvovaginal Candidiasis (Yeast Infection):**
                   • **Presentation:** Thick, white, clumpy discharge resembling cottage cheese. Intense vulvar pruritus (itching), erythema, and dysuria. Strictly **no amine odor**.
                   • **Therapy:** OTC topical or intravaginal clotrimazole/miconazole, or single oral Fluconazole 150 mg.
                3. **Bacterial Vaginosis (BV):**
                   • **Presentation:** Thin, homogeneous, watery grayish-white discharge with a distinct **fishy amine odor** (accentuated after intercourse or menses). Minimal vulvar itching.
                   • **Microbiology:** Depletion of *Lactobacillus* species with overgrowth of anaerobic *Gardnerella vaginalis* and *Atopobium vaginae*. Vaginal pH > 4.5.
                   • **Therapy:** Oral or intravaginal Metronidazole (500mg BID x 7 days) or Clindamycin.
                4. **Trichomoniasis (STI):**
                   • **Presentation:** Copious, frothy, yellow-green malodorous discharge, vulvar irritation, and "strawberry cervix" (punctate hemorrhages). Requires oral Metronidazole for both patient and partner.

                #### 💡 Microbiome Preservation Principles:
                Never douche or use fragranced soaps; douching strips the protective lactic-acid mantle and elevates risk of BV and pelvic inflammatory disease (PID) tenfold.$citation
            """.trimIndent()
        }

        // 2. Pelvic Floor Health, Kegels vs Reverse Kegels & Sexual Comfort
        if (containsAny(lower, "pelvic floor", "kegel", "pain with sex", "dyspareunia", "vaginismus", "tight pelvic floor", "incontinence", "prolapse")) {
            return """
                ### 🩺 Clinical Physical Therapy: Pelvic Floor Biomechanics & Function
                **Status:** Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 Hypertonic vs. Hypotonic Pelvic Floor:
                • **Hypertonic (Overactive / Too Tight):**
                  - **Presentation:** Dyspareunia (pain with penetration), chronic pelvic pain, feeling of incomplete bladder emptying, constipation, tailbone pain, or urinary hesitancy.
                  - **CRITICAL WARNING:** **DO NOT DO KEGELS!** Kegel contractions in a hypertonic floor exacerbate myofascial trigger points and worsen pain.
                  - **Prescribed Therapy:** **Down-training & Reverse Kegels.** 360-degree diaphragmatic breathing, pelvic floor drops, happy baby pose, deep squat stretches, and specialized pelvic floor physical therapy.
                • **Hypotonic (Underactive / Weak):**
                  - **Presentation:** Stress urinary incontinence (leaking with coughing, sneezing, jumping), feelings of vaginal heaviness or pelvic organ prolapse.
                  - **Prescribed Therapy:** Progressive neuromuscular re-education, coordinated Kegel activations paired with transverse abdominis recruitment.

                #### 🔬 Intimate Comfort & Lubrication:
                For dyspareunia, select isotonic lubricants formulated within physiologic vaginal osmolarity (<380 mOsm/kg) and pH (3.8–4.2) to prevent epithelial desquamation.$citation
            """.trimIndent()
        }

        // 3. Period Products: Menstrual Cups, Discs, Tampons & Toxic Shock Syndrome (TSS)
        if (containsAny(lower, "tampon", "cup", "menstrual cup", "disc", "menstrual disc", "tss", "toxic shock", "period underwear", "pad")) {
            return """
                ### 🩺 Clinical Hygiene & Gynecology: Menstrual Management & Safety
                **Status:** Day $cycleDayInt (${prediction.phase} Phase)

                #### 🩸 Product Comparison Matrix:
                1. **Menstrual Cups (Vaginal Canal Placement):**
                   • Made of medical-grade silicone or TPE. Creates a gentle airtight seal in the vaginal canal below the cervix.
                   • Holds 20–35 mL of fluid (3–5x a regular tampon). Safe for up to **12 hours** of continuous wear.
                2. **Menstrual Discs (Vaginal Fornix Placement):**
                   • Sits diagonally in the vaginal fornix directly beneath the cervix, tucked behind the pubic bone.
                   • Flat-fit design leaves the vaginal canal completely unobstructed, enabling **mess-free period intimacy**.
                   • High capacity (30–60 mL).
                3. **Tampons & Toxic Shock Syndrome (TSS) Prevention:**
                   • **TSS Mechanism:** Caused by superantigen toxins produced by *Staphylococcus aureus* or *Streptococcus pyogenes* proliferating in high-absorbency synthetic fibers.
                   • **Golden Safety Rules:** Never use higher absorbency than required by your current flow. Change tampons every **4 to 8 hours max**. Never leave a tampon in overnight if sleeping >8 hours.
                   • **TSS Warning Red Flags:** Sudden high fever (>102°F), hypotension (dizziness, fainting), vomiting, diarrhea, and a diffuse sunburn-like macular rash.$citation
            """.trimIndent()
        }

        // 4. Heavy Menstrual Bleeding (Menorrhagia & PALM-COEIN Classification)
        if (containsAny(lower, "heavy period", "heavy bleeding", "bleeding a lot", "blood clots", "menorrhagia", "soaking pads", "anemia", "ferritin", "iron")) {
            return """
                ### 🩺 Clinical Hematology & Gynecology: Heavy Menstrual Bleeding (PALM-COEIN)
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 FIGO Diagnostic Definition of Abnormal Uterine Bleeding (AUB):
                Excessive menstrual blood loss (>80 mL per cycle) or bleeding that interferes with physical, social, emotional, or material quality of life.
                • **Clinical Thresholds:** Soaking through one or more sanitary pads/tampons every hour for 2 or more consecutive hours; needing double protection; passing blood clots larger than a quarter (approx. 2.5 cm); bleeding lasting >7 days.

                #### 🔬 FIGO PALM-COEIN Classification Etiology:
                • **Structural Causes (PALM):**
                  - **P**olyp (Endometrial polyps)
                  - **A**denomyosis (Endometrial tissue within myometrium)
                  - **L**eiomyoma (Uterine fibroids, especially submucosal)
                  - **M**alignancy & Hyperplasia (Atypical endometrial hyperplasia)
                • **Non-Structural Causes (COEIN):**
                  - **C**oagulopathy (von Willebrand disease, platelet dysfunction)
                  - **O**vulatory dysfunction (PCOS, thyroid, luteal phase defect)
                  - **E**ndometrial (Impaired local hemostasis/endothelin production)
                  - **I**atrogenic (Anticoagulants, copper IUD)
                  - **N**ot yet classified

                #### 🩸 Hematological Evaluation & Iron Protocol:
                • **Ferritin Testing:** Serum ferritin is the earliest biomarker of iron depletion. Target ferritin is **>50 ng/mL** (standard reference range lower bounds of 10–15 ng/mL flag only severe exhaustion of iron stores).
                • **Medical Management:** First-line non-hormonal treatment includes **Tranexamic Acid** (antifibrinolytic) or NSAIDs (Naproxen sodium) taken during flow to reduce blood volume by 30–50%.$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
