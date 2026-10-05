package com.example.ai

import com.example.data.model.UserEntity
import com.example.engine.PredictionResult

/**
 * Specialized Clinical Knowledge Domain:
 * Perimenopause, Menopause, Thyroid, Weight Management, Infradian Fasting & Supplements.
 */
object HormonesAndMetabolismKnowledge {

    fun matchAndRespond(
        query: String,
        user: UserEntity,
        prediction: PredictionResult,
        cycleDayInt: Int,
        citation: String
    ): String? {
        val lower = query.lowercase().trim()

        // 1. Perimenopause & Menopause Transition
        if (containsAny(lower, "perimenopause", "menopause", "hot flash", "night sweat", "hrt", "hormone replacement", "estrogen drop", "straw criteria")) {
            return """
                ### 🩺 Clinical Endocrinology: Perimenopause & The Climacteric Transition
                **Patient:** ${user.name.ifBlank { "Patient" }} | Day $cycleDayInt (${prediction.phase} Phase)

                #### 🧬 The STRAW+10 Staging System:
                • **Early Menopausal Transition (Stage -2):** Variable cycle lengths with persistent differences of ≥7 days in consecutive cycles.
                • **Late Menopausal Transition (Stage -1):** An interval of amenorrhea of ≥60 days. Marked by profound erratic fluctuations in 17β-estradiol and rising follicle-stimulating hormone (FSH >25–30 mIU/mL).
                • **Natural Menopause (Stage 0):** Clinically confirmed after **12 consecutive months of spontaneous amenorrhea** without another biological or physiological cause. Average onset is 51.4 years.

                #### 🔬 Pathophysiology of Vasomotor Symptoms (Hot Flashes):
                Rapid decline in estrogen narrows the hypothalamic thermoneutral zone. Normally, minor shifts in ambient or core temperature trigger no autonomic response. In estrogen deficiency, even a 0.05°C increase triggers intense peripheral cutaneous vasodilation, diaphoresis (sweating), and tachycardia.

                #### 💊 Modern Evidence-Based Therapeutics (NAMS / Endocrine Society):
                1. **Menopausal Hormone Therapy (MHT / HRT):**
                   • Transdermal 17β-Estradiol (patch or gel): Bypasses hepatic first-pass metabolism, eliminating increased venous thromboembolism (VTE) risk.
                   • Micronized Progesterone (100–200 mg nightly): Mandatory for women with an intact uterus to prevent endometrial hyperplasia and adenocarcinoma.
                2. **Genitourinary Syndrome of Menopause (GSM):** Local ultra-low-dose vaginal estradiol cream, tablet, or ring restores glycogenated stratified squamous epithelium and acidic vaginal pH (3.8–4.5) without systemic absorption.
                3. **Bone Density Protection:** In the first 5 years post-menopause, women lose up to 10% of trabecular bone. Prioritize resistance weight training, Vitamin D3 (2000–5000 IU), and Vitamin K2 (MK-7 100 mcg).$citation
            """.trimIndent()
        }

        // 2. Thyroid Dynamics (Hypothyroidism & Hyperthyroidism)
        if (containsAny(lower, "thyroid", "tsh", "hashimoto", "hypothyroid", "hyperthyroid", "cold hands", "sluggish thyroid")) {
            return """
                ### 🩺 Clinical Neuroendocrinology: Thyroid-Ovarian Axis
                **Patient:** ${user.name.ifBlank { "Patient" }}

                #### 🧬 Mechanisms of Thyroid-Induced Cycle Disruptions:
                Thyroid hormones (T3 and T4) act synergistically with FSH and LH on ovarian granulose cells to regulate steroidogenesis:
                1. **Hypothyroidism (Elevated TSH, Low Free T4):**
                   • **TRH Elevation & Hyperprolactinemia:** Low circulating T4 causes the hypothalamus to oversecrete Thyrotropin-Releasing Hormone (TRH). TRH directly stimulates pituitary lactotrophs, elevating **Prolactin**. Hyperprolactinemia inhibits pulsatile GnRH, causing oligomenorrhea or amenorrhea.
                   • **Coagulopathy & Menorrhagia:** Hypothyroidism reduces coagulation factors VII, VIII, IX, and von Willebrand factor, frequently causing profound, heavy menorrhagia.
                2. **Hashimoto's Autoimmune Thyroiditis:** Presence of anti-Thyroid Peroxidase (TPO) and anti-Thyroglobulin (Tg) antibodies. Strongly correlated with increased risk of recurrent pregnancy loss and subfertility.

                #### 🧪 Optimal Endocrine Diagnostic Targets:
                • **TSH:** Optimal preconception and fertility target is **1.0–2.0 mIU/L** (standard laboratory reference ranges frequently extend up to 4.5–5.0 mIU/L).
                • **Free T3 & Free T4:** Evaluates bioavailable active thyroid hormones.
                • **Reverse T3:** Screens for conversion impairment under chronic physical or emotional stress.$citation
            """.trimIndent()
        }

        // 3. Infradian Fasting, Weight Management & Metabolism
        if (containsAny(lower, "fasting", "intermittent fasting", "weight loss", "lose weight", "diet plan", "metabolism", "belly fat", "insulin resistance")) {
            val fastingGuidance = when (prediction.phase) {
                "Follicular", "Ovulatory" -> """
                    • **Metabolic Status:** High estrogen enhances insulin sensitivity, increases glucose uptake via GLUT4 translocation, and supports metabolic flexibility.
                    • **Fasting Recommendation:** Intermittent fasting (14:10 to 16:8) is well-tolerated. High training capacity and fat oxidation are optimal.
                """.trimIndent()
                else -> """
                    • **Metabolic Status:** Elevated progesterone promotes mild physiological insulin resistance and raises Resting Energy Expenditure by **150–300 kcal/day**.
                    • **Fasting Recommendation:** **Avoid extended fasts (>13–14 hours)!** Prolonged fasting in the luteal phase triggers hypothalamic kisspeptin downregulation, spikes cortisol, and exacerbates reactive hypoglycemia and binge cravings.
                """.trimIndent()
            }

            return """
                ### ⚖️ Clinical Infradian Metabolism & Nutritional Physiology
                **Status:** Day $cycleDayInt of your **${prediction.phase} Phase**

                #### 🔬 Phase-Specific Metabolic Dynamics:
                $fastingGuidance

                #### 💡 Evidence-Based Body Composition Strategy:
                1. **Protein Threshold (1.6–2.2 g/kg body weight):** Distribute across 3–4 meals containing at least 30g of protein with 2.5g leucine to trigger muscle protein synthesis (mTOR).
                2. **Fiber Density (>35g daily):** Soluble fiber binds biliary estrogen metabolites in the intestinal lumen, ensuring definitive excretion via stool rather than reabsorption via bacterial beta-glucuronidase.
                3. **Glycemic Sequencing:** Eat vegetables/fiber first, proteins and fats second, and starches/carbohydrates last to reduce postprandial glucose excursions by up to 73%.$citation
            """.trimIndent()
        }

        // 4. Evidence-Based Hormonal Supplements
        if (containsAny(lower, "supplement", "vitamin", "inositol", "vitex", "chasteberry", "dim", "coq10", "nac", "zinc", "magnesium", "b6")) {
            return """
                ### 💊 Clinical Micronutrition: Evidence-Based Hormonal Supplements
                **Status:** Day $cycleDayInt (${prediction.phase} Phase)

                #### 🔬 Monograph of Key Hormonal Therapeutics:
                1. **Myo-Inositol & D-Chiro-Inositol (40:1 Ratio, 2000mg BID):** The gold standard clinical nutrient for restoring ovulatory frequency, down-regulating theca cell androgen production, and sensitizing insulin receptors in metabolic and ovulatory dysregulation.
                2. **Vitex Agnus-Castus (Chasteberry 400 mg daily):** Acts on dopamine D2 receptors in the anterior pituitary to lower mild hyperprolactinemia, thereby lengthening a shortened luteal phase and enhancing corpus luteum progesterone synthesis.
                3. **Diindolylmethane (DIM 100–200 mg):** A natural derivative of indole-3-carbinol found in cruciferous vegetables. Shifts hepatic estrogen metabolism from the inflammatory 16α-hydroxyestrone pathway toward the protective, cardioprotective 2-hydroxyestrone pathway.
                4. **Magnesium Bisglycinate (300–400 mg elemental):** Relaxes uterine and vascular smooth muscle, crosses blood-brain barrier to upregulate GABA-A activity, dampening PMS anxiety, cramping, and migraine susceptibility.
                5. **Coenzyme Q10 (Ubiquinol 200 mg daily):** Mitochondrial antioxidant proven to combat oxidative age-related oocyte deterioration and optimize follicular fluid quality.$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
