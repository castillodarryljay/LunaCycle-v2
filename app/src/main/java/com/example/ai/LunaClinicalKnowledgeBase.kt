package com.example.ai

/**
 * ClinicalUserContext:
 * Snapshot of patient's current biomarkers and reproductive status passed into the clinical engine.
 */
data class ClinicalUserContext(
    val userName: String,
    val cycleDay: Int,
    val phase: String,
    val nextPeriodDate: String,
    val daysUntilPeriod: Int,
    val fertilityStatus: String,
    val todayWaterMl: Int,
    val waterGoalMl: Int,
    val todaySymptoms: List<String>,
    val todayMood: String?,
    val todayFlow: String,
    val isPeriodToday: Boolean,
    val todayBBT: Float?,
    val todayMucus: String?,
    val isIrregular: Boolean,
    val baselineCycleLength: Int
)

/**
 * LunaClinicalKnowledgeBase:
 * Comprehensive clinical knowledge base covering Gynecology, Reproductive Endocrinology,
 * Gynecological Infections, Menopause, Abnormal Bleeding, and Laboratory Diagnostics.
 */
object LunaClinicalKnowledgeBase {

    fun findClinicalMatch(lower: String, ctx: ClinicalUserContext): String? {
        val citation = "\n\n*[Luna Ultra AI v4.0 • Clinical Endocrinology & Gynecology Engine • 100% On-Device]*"

        // 1. PCOS (Polycystic Ovary Syndrome)
        if (containsAny(lower, "pcos", "polycystic", "cysts on ovaries", "hirsutism", "excess hair", "high androgen", "high testosterone", "irregular period pcos")) {
            return """
                ### 🩺 Clinical Evaluation: Polycystic Ovary Syndrome (PCOS)
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase}) | **Baseline Cycle:** ${ctx.baselineCycleLength} days

                #### 🧬 Diagnostic Criteria & Pathophysiology (Rotterdam 2003 / 2023 Guidelines):
                Diagnosis requires at least two of the following three features:
                1. **Oligo- or Anovulation:** Delayed or absent follicular rupture leading to irregular cycles (>35 days) or amenorrhea.
                2. **Hyperandrogenism:** Clinical (hirsutism on chin/chest/abdomen, cystic acne, androgenic alopecia) or biochemical (elevated Free Testosterone, DHEA-S, or Androstenedione).
                3. **Polycystic Ovarian Morphology:** Ultrasound showing ≥20 follicles (2–9 mm) per ovary or ovarian volume ≥10 mL ("string-of-pearls" appearance).

                #### 🔬 Core Endocrine Drivers:
                • **Insulin Resistance:** Elevated fasting insulin stimulates ovarian theca cells to overproduce androgens while simultaneously downregulating hepatic **Sex Hormone-Binding Globulin (SHBG)**, resulting in high levels of circulating biologically active free testosterone.
                • **Altered LH:FSH Pulsatility:** Rapid GnRH pulses from the hypothalamus favor pituitary LH secretion over FSH, arresting follicular development at the pre-antral stage.

                #### 💊 Evidence-Based Management Protocols:
                1. **Myo-Inositol & D-Chiro-Inositol (40:1 physiological ratio):** 2,000 mg Myo-inositol + 50 mg D-chiro-inositol twice daily. Acts as a second messenger in cellular insulin signaling and restores spontaneous ovulatory frequency.
                2. **Organic Spearmint Tea (Mentha spicata):** 2 cups daily demonstrates clinically proven anti-androgenic activity, lowering free testosterone and mitigating hirsutism.
                3. **Glycemic Architecture:** Pair all carbohydrates with high-biological-value protein and dietary fiber (glucose buffering) to avoid postprandial insulin surges.
                4. **Resistance Training:** 3 sessions/week increases GLUT4 receptor translocation in skeletal muscle, improving systemic insulin sensitivity independent of body weight.$citation
            """.trimIndent()
        }

        // 2. ENDOMETRIOSIS & ADENOMYOSIS
        if (containsAny(lower, "endometriosis", "endo", "adenomyosis", "chocolate cyst", "endometrioma", "retrograde menstruat", "pain with sex", "dyspareunia", "pain with bowel", "dyschezia")) {
            return """
                ### 🩺 Clinical Gynecology: Endometriosis & Adenomyosis Evaluation
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🧬 Pathophysiology & Structural Phenotypes:
                • **Endometriosis:** Functional endometrial-like glands and stroma implant outside the uterine cavity (most commonly on ovaries, pelvic peritoneum, uterosacral ligaments, and pouch of Douglas). These ectopic implants undergo cyclical bleeding during each menstrual cycle, eliciting intense macrophage activation, cytokine cascades (IL-1β, TNF-α), severe chronic pelvic pain, and fibrotic adhesions.
                • **Adenomyosis:** Direct invasion of basal endometrium into the myometrium (uterine muscular wall), inducing reactive smooth muscle hypertrophy. Classically presents as a globally enlarged, "boggy," tender uterus with severe secondary dysmenorrhea and heavy menorrhagia.

                #### 🚩 Classic Symptom Quadrad ("The 4 Ds"):
                1. **Severe Dysmenorrhea:** Pelvic cramping unresponsive to standard NSAIDs.
                2. **Deep Dyspareunia:** Pain during or after penetrative intercourse due to uterosacral ligament involvement.
                3. **Dyschezia:** Painful defecation, especially during menses (rectovaginal septum disease).
                4. **Dysuria:** Cyclical urinary pain or urgency.

                #### 🔬 Diagnostic Gold Standards & Multimodal Management:
                1. **Diagnostics:** High-resolution specialized transvaginal pelvic ultrasound (looking for adenomyotic subendometrial cysts, "comet-tail" artifacts, and ovarian endometriomas) and Pelvic MRI. The definitive diagnostic standard remains diagnostic laparoscopy with histological confirmation.
                2. **First-Line Suppression:** Continuous progestin therapy (e.g., Dienogest 2 mg, Levonorgestrel IUD, or continuous oral contraceptives) to induce decidualization and atrophy of ectopic lesions.
                3. **Pelvic Floor Physical Therapy (PFPT):** Evaluates and releases hypertonic pelvic floor muscles and myofascial trigger points that develop secondary to chronic pelvic pain.
                4. **Anti-Inflammatory Nutrition:** High EPA/DHA Omega-3s (2,000 mg/day), Curcumin (with piperine for bioavailability), and elimination of ultra-processed trans-fats to downregulate pro-inflammatory eicosanoids.$citation
            """.trimIndent()
        }

        // 3. VAGINAL INFECTIONS & MICROBIOME (BV, YEAST, UTI)
        if (containsAny(lower, "yeast infection", "thrush", "candida", "bv", "bacterial vaginosis", "uti", "urinary tract", "burning pee", "smelly discharge", "fishy odor", "cottage cheese discharge", "vaginal itching", "vulva itch")) {
            return """
                ### 🩺 Clinical Micro-Ecology: Vaginal & Urinary Health Differential
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🔬 Differential Diagnostic Matrix:
                1. **Bacterial Vaginosis (BV):**
                   • **Etiology:** Depletion of protective *Lactobacillus* species (*L. crispatus*) and overgrowth of polymicrobial anaerobes (*Gardnerella vaginalis*, *Atopobium vaginae*).
                   • **Hallmarks:** Thin, watery, homogenous grayish-white discharge with a distinctive "fishy" odor that intensifies after intercourse or menses; elevated vaginal pH (>4.5); clue cells on wet mount.
                   • **Treatment:** Metronidazole 500 mg orally BID for 7 days or Metronidazole 0.75% vaginal gel.
                2. **Vulvovaginal Candidiasis (Yeast):**
                   • **Etiology:** Overgrowth of fungal blastospores and pseudohyphae (*Candida albicans* in 85–90%).
                   • **Hallmarks:** Thick, white, curdy "cottage-cheese" discharge; intense vulvar pruritus, burning, erythema, and edema; **normal vaginal pH (4.0–4.5)**.
                   • **Treatment:** Oral Fluconazole 150 mg single dose or topical azole creams (Clotrimazole, Miconazole) for 3–7 days.
                3. **Urinary Tract Infection (Cystitis):**
                   • **Etiology:** Uropathogenic *Escherichia coli* ascending via the urethra.
                   • **Hallmarks:** Dysuria (sharp burning during micturition), urinary urgency, high frequency, suprapubic ache, cloudy or hematuric urine.
                   • **Protocol:** Dipstick urinalysis for leukocyte esterase and nitrites; midstream urine culture; targeted antibiotics (Nitrofurantoin, Fosfomycin, or TMP-SMX).
                   • **Prophylaxis:** D-Mannose 2,000 mg daily prevents bacterial adhesin (FimH) attachment to the urothelial lining.

                #### ⚠️ Red Flag Warning:
                If urinary burning is accompanied by high fever (>100.4°F / 38°C), chills, nausea/vomiting, or severe flank/lower back pain (Costovertebral Angle tenderness), seek immediate medical evaluation to rule out **Acute Pyelonephritis** (kidney infection).$citation
            """.trimIndent()
        }

        // 4. FERTILITY, PREGNANCY, IMPLANTATION & CONCEPTION
        if (containsAny(lower, "implantation", "implantation bleeding", "pregnant right after period", "pregnant during period", "early pregnancy signs", "hcg", "positive test", "chemical pregnancy", "miscarriage", "conceive", "trying to conceive", "ttc")) {
            return """
                ### 🩺 Clinical Reproductive Physiology: Conception & Implantation
                **Patient:** ${ctx.userName} | **Cycle Day:** ${ctx.cycleDay} | **Fertility Rating:** ${ctx.fertilityStatus}

                #### 🧬 Implantation Bleeding vs. Menstrual Period:
                • **Timing:** Implantation occurs **6 to 12 days post-ovulation** (average 8–10 days) when the blastocyst apposes and invades the receptive decidua basalis.
                • **Flow Characteristics:** Implantation bleeding is light spotting (pink or brown streaks), lasting only a few hours to 2 days, and **never contains tissue clots or heavy red flow**.
                • **Menstrual Flow:** Starts light, turns into heavier bright-red flow within 24 hours, often accompanied by myometrial prostaglandins and uterine cramping.

                #### 🧪 Testing Window & hCG Kinetics:
                • **Urine hCG Detection:** Syncytiotrophoblast cells begin producing Human Chorionic Gonadotropin (hCG) only *after* implantation is complete. Serum hCG doubles every 48 hours in viable early intrauterine gestations.
                • **Optimal Test Timing:** High-sensitivity home pregnancy tests (threshold 10–25 mIU/mL) reach >99% accuracy when taken on or after the first day of the missed period using first-morning concentrated urine.

                #### ❓ Can You Get Pregnant During or Right After Your Period?
                • **Yes, under specific cycle dynamics!** Human sperm can survive inside fertile cervical crypt mucus for **up to 5 days (120 hours)**. If an individual has a short menstrual cycle (e.g., 21–24 days) or delayed/prolonged menses, ovulation can occur as early as Cycle Day 8–10. Intercourse occurring toward the tail end of bleeding can result in viable sperm waiting in the fallopian ampulla when the oocyte is released.

                #### 💊 Pre-Conception Clinical Checklist (TTC):
                1. **Methylated Folate (L-Methylfolate 400–800 mcg):** Superior to synthetic folic acid for individuals with MTHFR enzymatic polymorphisms; prevents neural tube defects.
                2. **CoQ10 / Ubiquinol (200–400 mg daily):** Improves mitochondrial ATP production within oocytes, supporting chromosomal spindle fidelity and blastocyst maturation.
                3. **Ovulation Confirmation:** Sustained basal body temperature (BBT) elevation for ≥3 consecutive days confirms successful corpus luteum formation and progesterone production.$citation
            """.trimIndent()
        }

        // 5. PERIMENOPAUSE & MENOPAUSE (STRAW+10)
        if (containsAny(lower, "perimenopause", "menopause", "hot flash", "night sweat", "estrogen drop", "hrt", "hormone replacement", "vaginal dryness", "straw", "skipped period 40s", "skipped period 50s")) {
            return """
                ### 🩺 Clinical Endocrinology: Perimenopause & Menopause Transition
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🧬 The STRAW+10 Staging Framework:
                1. **Early Perimenopause (Stage -2):** Persistent difference of ≥7 days in the length of consecutive menstrual cycles. Follicle count declines, causing compensatory elevations in pituitary FSH and fluctuating, unpredictable estradiol peaks.
                2. **Late Perimenopause (Stage -1):** Amenorrhea intervals spanning ≥60 days. Cycles become predominantly anovulatory with marked progesterone deficiency.
                3. **Menopause (Stage 0):** Clinically diagnosed retrospectively after **12 consecutive months of permanent amenorrhea** in the absence of other physiological or pharmacological causes (average age 51.4 years).

                #### 🔬 Pathophysiology of Common Symptoms:
                • **Vasomotor Symptoms (Hot Flashes/Flushes):** Fluctuating estradiol destabilizes thermoregulatory set-points in the preoptic hypothalamus. Hypertrophy of **KNDy neurons** (expressing Kisspeptin, Neurokinin B, and Dynorphin) triggers inappropriate peripheral vasodilation and profuse diaphoresis.
                • **Genitourinary Syndrome of Menopause (GSM):** Estrogen receptor depletion in the lower third of the vagina and vulva causes epithelial thinning, loss of elasticity, loss of rugae, elevated vaginal pH (>5.0), and dyspareunia.

                #### 💊 Modern Evidence-Based Therapeutics (NAMS / Endocrine Society):
                1. **Menopausal Hormone Therapy (MHT / HRT):**
                   • **Transdermal 17β-Estradiol (Patch or Gel):** Directly enters systemic circulation without first-pass hepatic metabolism, avoiding elevation of hepatic clotting factors and providing superior thrombotic safety.
                   • **Micronized Progesterone (100–200 mg orally at bedtime):** Compulsory for anyone with an intact uterus to counteract estradiol-induced endometrial hyperplasia and reduce endometrial cancer risk. Promotes GABAergic slow-wave sleep.
                2. **Non-Hormonal Neurokinin 3 Receptor Antagonists (Fezolinetant):** Directly blocks NKB binding on KNDy neurons, reducing hot flash frequency by >60% without systemic hormones.
                3. **Vaginal Estrogen Creams / Inserts (10 mcg):** Negligible systemic absorption; fully restores local mucosal architecture, glycogen stores, and beneficial *Lactobacillus* colonization.$citation
            """.trimIndent()
        }

        // 6. ABNORMAL UTERINE BLEEDING & FLOW IRREGULARITIES (PALM-COEIN)
        if (containsAny(lower, "heavy period", "heavy bleeding", "menorrhagia", "clots", "blood clots", "brown blood", "spotting", "bleeding between periods", "metrorrhagia", "flooding", "soaking pad")) {
            return """
                ### 🩺 Clinical Gynecology: Abnormal Uterine Bleeding (AUB) Analysis
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase}) | **Today's Flow:** ${ctx.todayFlow}

                #### 🧬 FIGO PALM-COEIN Diagnostic Classification:
                Abnormal bleeding is categorized into structural and non-structural etiologies:
                • **P**olyp (Endometrial or endocervical glandular overgrowth).
                • **A**denomyosis (Endometrial invasion into uterine muscular wall).
                • **L**eiomyoma / Fibroids (Submucosal fibroids distort the endometrial cavity).
                • **M**alignancy & Hyperplasia (Requires endometrial biopsy if persistent or risk factors present).
                • **C**oagulopathy (e.g., von Willebrand disease in adolescent heavy flow).
                • **O**vulatory Dysfunction (Lack of corpus luteum leads to unopposed estrogen and breakthrough bleeding).
                • **E**ndometrial (Localized hemostatic defects in prostaglandins and endothelin-1).
                • **I**atrogenic (Contraceptive implants, copper IUDs, anticoagulants).
                • **N**ot yet classified.

                #### 🩸 What Does Brown Blood Mean?
                Blood exposed to the acidic vaginal environment and ambient oxygen undergoes conversion of ferrous iron (Fe²⁺) in hemoglobin to ferric iron (Fe³⁺) (**methemoglobin**). This oxidation turns the blood dark brown or blackish. It indicates slow transit through the cervix and is entirely normal at the start or end of menses.

                #### ⚠️ Menorrhagia Red Flags (Seek Prompt Clinical Attention):
                • Soaking through one or more heavy pads or tampons every hour for 2 consecutive hours.
                • Passing blood clots larger than a quarter (25 mm diameter).
                • Symptoms of symptomatic acute blood loss: dizziness, orthostatic lightheadedness, pale conjunctiva, resting tachycardia, or severe fatigue.
                • Complete laboratory evaluation: **CBC (Hemoglobin/Hematocrit), Serum Ferritin, and Pelvic Ultrasound** are recommended.$citation
            """.trimIndent()
        }

        // 7. THYROID, METABOLISM & AUTOIMMUNITY
        if (containsAny(lower, "thyroid", "hypothyroid", "hyperthyroid", "hashimoto", "tsh", "free t3", "free t4", "weight gain period", "cold hands", "hair loss period")) {
            return """
                ### 🩺 Clinical Endocrinology: Thyroid Axis & Menstrual Regulation
                **Patient:** ${ctx.userName} | **Current Phase:** Day ${ctx.cycleDay} (${ctx.phase})

                #### 🧬 The Hypothalamic-Pituitary-Thyroid-Ovarian Nexus:
                Thyroid hormones (T3 and T4) synergize with FSH to stimulate granulosa cell differentiation, aromatase activity, and progesterone secretion by the corpus luteum:
                1. **Hypothyroidism (High TSH, Low Free T4):**
                   • Elevated **Thyrotropin-Releasing Hormone (TRH)** stimulates pituitary lactotroph cells to secrete excess **Prolactin (Hyperprolactinemia)**.
                   • High prolactin inhibits pulsatile GnRH secretion, leading to anovulation, luteal phase defect, oligomenorrhea, or secondary amenorrhea.
                   • Impaired hepatic synthesis of **SHBG** elevates free unbound estrogen, promoting endometrial hyperplasia and heavy, prolonged menses (**Menorrhagia**).
                2. **Hashimoto's Thyroiditis:**
                   • Autoimmune destruction of thyroid follicular cells by Anti-Thyroperoxidase (Anti-TPO) and Anti-Thyroglobulin (Anti-Tg) antibodies. Often co-occurs with systemic fatigue, cold intolerance, constipation, brain fog, and eyebrow thinning.

                #### 🧪 Complete Clinical Thyroid Panel:
                • **TSH (Thyroid-Stimulating Hormone):** Standard reference range is 0.45–4.5 mIU/L; however, preconception and reproductive endocrinology guidelines recommend an optimal target of **1.0–2.5 mIU/L**.
                • **Free T4 and Free T3:** Evaluates peripheral conversion and bioavailable cellular hormone.
                • **Thyroid Peroxidase Antibodies (Anti-TPO):** Screens for autoimmune thyroiditis.
                • **Nutritional Cofactors for T4 to T3 Conversion:** Selenium (200 mcg/day via 2 Brazil nuts), Zinc Picolinate (30 mg/day), and Iron/Ferritin (>50 ng/mL required for thyroid peroxidase enzyme function).$citation
            """.trimIndent()
        }

        return null
    }

    private fun containsAny(text: String, vararg keywords: String): Boolean {
        return keywords.any { text.contains(it, ignoreCase = true) }
    }
}
