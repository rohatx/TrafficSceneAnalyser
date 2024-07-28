# place holder document for the documentation of the analysis process
## future documentation includes:
* prompt engineering process
* sanity check implementation

  * accuracy of sanity check (FP/FN, TP/TN)
  * abnormalities?
  * when does the sanity check work well?

* how where the LMMs tested? 

  * categorized images
  * table of LLM  (GPT-4o as the primary example)
  *  table contains:
    
   | image name | sanity check output | LMM output | 
   | :--------: | :-----------------: | :--------: |

temporary table for accuracy and latency of the LMM:

| LMM | accuracy | latency |
| :-: | :------: | :-----: |
| GPT-4o | 94% | 13 seconds |
| Claude | 90% | 5 seconds |
| Gemini Pro | 84% | 10 seoncds |
| Gemini Flash | 60% | 7 seconds |
| LLaVA | 68% | 30 seconds |
| FireLLaVA | 80% | 10 seconds |

