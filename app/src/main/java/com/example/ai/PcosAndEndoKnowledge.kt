package com.example.ai

import com.example.data.model.LogEntity
import com.example.data.model.UserEntity
import com.example.engine.PredictionResult

/**
 * Specialized Clinical Knowledge Domain:
 * Polycystic Ovary Syndrome (PCOS), Endometriosis, Adenomyosis & Ovarian Cysts.
 */
object PcosAndEndoKnowledge {

    fun matchAndRespond(
        query: String,
        user: UserEntity,
        prediction: PredictionResult,
        cycleDayInt: Int,
        citation: String
    ): String? {
        val lower = query.lowercase().trim()

        // 1. PCOS (Polycystic Ovary Syndrome)
        if (containsAny(lower, "pcos", "polycystic", "cysts on ovaries", "hirsutism", "facial hair", "irregular periods pcos", "androgen")) {
            return """
                ### 🩺 Clinical Endocrinology: Polycystic Ovary Syndrome (PCOS)
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 Diagnostic Criteria (Rotterdam Consensus):
                Diagnosis requires at least 2 of the following 3 features (with exclusion of other etiologies like thyroid disease or hyperprolactinemia):
                1. **Oligo- or Anovulation:** Infrequent menstrual periods (>35-day intervals) or complete amenorrhea (>3 months).
                2. **Clinical or Biochemical Hyperandrogenism:** Elevated free testosterone, DHEA-S, or physical manifestations (hirsutism on chin/chest/abdomen, cystic jawline acne, androgenic alopecia).
                3. **Polycystic Ovarian Morphology:** ≥20 follicles (2–9 mm) per ovary or ovarian volume >10 mL on transvaginal ultrasound ("string of pearls" sign).

                #### 🔬 Core Pathophysiology (The Insulin-Androgen Axis):
                • **Insulin Resistance (70-80% of patients):** Hyperinsulinemia acts directly on ovarian theca cells to upregulate CYP17A1 enzyme activity, excessively synthesizing androgens.
                • **Suppressed SHBG:** High circulating insulin lowers hepatic Sex Hormone-Binding Globulin synthesis, drastically increasing biologically active **free testosterone**.
                • **Arrested Follicular Development:** High local androgens prevent follicle selection, leading to multiple immature antral follicles and absent LH surges.

                #### 💊 Evidence-Based Therapeutics:
                1. **Myo-Inositol & D-Chiro-Inositol (40:1 Physiological Ratio, 2000mg BID):** Second messenger for insulin and TSH. Restores ovulatory cyclicity in 65% of patients with metabolic PCOS within 12–16 weeks.
                2. **Organic Spearmint Infusion (Mentha spicata):** Drinking 2 cups daily for 30 days significantly reduces free testosterone by dampening 5α-reductase activity.
                3. **N-Acetylcysteine (NAC 600mg TID):** Powerful antioxidant precursor to glutathione; reduces insulin resistance and supports ovarian microcirculation.
                4. **Targeted Nutrition:** Emphasize high-fiber (>35g/day), anti-inflammatory foods with healthy fats (avocado, olive oil) paired with every carbohydrate to blunt glucose spikes.$citation
            """.trimIndent()
        }

        // 2. Endometriosis & Adenomyosis
        if (containsAny(lower, "endo", "endometriosis", "adenomyosis", "retrograde", "painful sex endo", "pelvic adhesion", "laparoscopy")) {
            return """
                ### 🩺 Clinical Gynecology: Endometriosis & Adenomyosis
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 Pathophysiology & Mechanisms:
                • **Endometriosis:** Functional endometrial glands and stroma implant outside the uterine cavity (peritoneum, ovaries, bowel, bladder). These ectopic lesions bleed cyclically in response to estrogen, causing chronic inflammation, fibrosis, neurogenesis (nerve proliferation), and debilitating pain.
                • **Adenomyosis:** Endometrial tissue invades deep into the uterine myometrium (muscle wall), causing an enlarged, tender, globular uterus, severe dysmenorrhea, and heavy menstrual bleeding (menorrhagia).

                #### 🔍 Clinical Presentations:
                1. **The Classic Symptom Triad:** Severe dysmenorrhea unresponsive to OTC NSAIDs, deep dyspareunia (pain with intercourse), and chronic pelvic pain.
                2. **Catamenial Symptoms:** Pain with urination (dysuria) or bowel movements (dyschezia) strictly synchronized with menstruation.
                3. **Sciatic Radiation:** Lesions irritating the lumbosacral plexus can cause cyclical buttock and leg pain.

                #### 🔬 Management & Standards of Care (ACOG / ESHRE):
                1. **Definitive Diagnosis:** Laparoscopic visualization with histological biopsy remains the gold standard. High-resolution pelvic MRI or specialized transvaginal ultrasound can detect deep infiltrating endometriosis (DIE) and adenomyotic myometrial heterogeneity.
                2. **Surgical Excision:** Cold-scissor/laser wide excision of ectopic lesions is superior to superficial ablation for pain recurrence.
                3. **Endocrine Suppression:** Continuous progestins (Dienogest 2mg daily or Levonorgestrel-releasing IUD) downregulate ectopic estrogen receptors and induce lesion atrophy.
                4. **Pelvic Floor Physical Therapy:** Crucial for down-training hypertonic pelvic floor spasms secondary to chronic visceral pain.$citation
            """.trimIndent()
        }

        // 3. Ovarian Cysts (Functional, Corpus Luteum, Dermoid, Endometrioma)
        if (containsAny(lower, "cyst", "ovarian cyst", "follicular cyst", "ruptured cyst", "sharp pelvic pain", "pelvic twinge")) {
            return """
                ### 🩺 Clinical Evaluation: Ovarian Cysts & Pelvic Pain
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 Types of Ovarian Cysts:
                1. **Follicular Cysts (Functional):** Occur when a Graafian follicle fails to rupture at ovulation and continues filling with transudate fluid. Typically resolve spontaneously within 1–3 cycles.
                2. **Corpus Luteum Cysts (Functional):** Form when the corpus luteum seals over after oocyte extrusion and fills with blood/serous fluid. Produces progesterone and can delay menstruation.
                3. **Endometriomas ("Chocolate Cysts"):** Ectopic endometrial tissue within the ovary accumulating thick, dark, oxidized blood. Associated with endometriosis.
                4. **Dermoid Cysts (Mature Teratoma):** Benign germ-cell tumors that may contain hair, sebum, or teeth.

                #### ⚠️ Clinical Red Flags / Ovarian Torsion:
                • **Acute Surgical Emergency:** Sudden, severe, unilateral lower abdominal pain associated with nausea, vomiting, and tachycardia can indicate **Ovarian Torsion** (twisting of the vascular pedicle), requiring immediate emergency ultrasound and laparoscopic detorsion to preserve ovarian viability.
                • **Rupture:** Sudden sharp pain followed by a dull pelvic ache and mild peritoneal irritation; usually managed conservatively if hemodynamically stable.$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
