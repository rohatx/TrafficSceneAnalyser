# B-BOSCH-05


# Documentation of the analysis process

### Contents

- [B-BOSCH-05](#b-bosch-05)
- [Documentation of the analysis process](#documentation-of-the-analysis-process)
    - [Contents](#contents)
  - [Prompt Engineering process](#prompt-engineering-process)
  - [Sanity Check implementation](#sanity-check-implementation)
    - [Accuracy of the Sanity Check](#accuracy-of-the-sanity-check)
    - [Abnormalities](#abnormalities)
    - [Performance of the Sanity Check](#performance-of-the-sanity-check)
  - [Testing Large Language Models](#testing-large-language-models)
    - [Testing conditions](#testing-conditions)
    - [Metric for accuracy](#metric-for-accuracy)
    - [Table for accuracy and latency of the LMMs](#table-for-accuracy-and-latency-of-the-lmms)

## Prompt Engineering process

Working with artificial intelligence system requires a prompt which is used to calculate the desired output the user (the prompt writer) wants to generate. The prompt details the requirements a result should contain.

In the case of the traffic analysis application, our developer team wants a LMM (large language model) to analyse an image typically depicting a traffic scene featuring a bridge or a tunnel. Based on the image, the choosen model should provide an output whether the object (e.g. bridge/tunnel) is underridable for a vehicle (truck/car). 

To get an utilisable reply, we decided to test out prompt engineering.
The starting point for the research was an [IBM article](https://www.ibm.com/de-de/topics/prompt-engineering) about prompt engineering. This article illustrates important aspects to consider when writing a prompt.
Artifical intelligence systems are usually trained with a purpose in mind. For example, ChatGPT was trained to process and compose textual data. Hence, the knowledge basis of various models are different and even different from the human mind. Since we do not know, what exactly the artifical intelligence systems knows, we want to engineer the prompt as precise as possible. We also etablished english as a good choice for a prompt as most models were trained in english.

Now with the fundamentels of our prompt determined, we looked for techniques on how to engineer the content of our prompt.
The [article from OpenAI](https://platform.openai.com/docs/guides/prompt-engineering/six-strategies-for-getting-better-results) illustrates different approaches on how to engineer a prompt.
An important aspect is balancing between shorter or longer prompts and hence giving or omitting contextual information. On one hand, short prompts can be processed more efficiently but the results may not be satisfying since the model lacks context to understand the data correctly. On the other hand, long prompts may take more time to be processed but result in a more precise reply. 

To find out which approach was more suited for our application, we engineered a short and a long prompt. Since both prompts worked equally well for our cause, we decided to use the shorter version to potentially speed up the reply time.

At this point we had engineered a solid prompt. As stated earlier, we want to process the output of the LMM. To ensure further usage, we wanted the reply to be in a certain .JSON format.
Inspired by this [bachelor thesis](https://www.diva-portal.org/smash/record.jsf?pid=diva2%3A1801354&dswid=-6570) we also engineered our prompt using the .JSON format directly. This approach worked very well throughout all LMMs we tested.

After all this research we finally had a prompt we were satisfied with. The structured prompt results in the correct format (albeit accuracy of the format depends on the model used) and the content was reasonable as well.


## Sanity Check implementation

To check the correctness of the output of the LMM, we implemented a so called "Sanity Check". 
The Sanity Check uses the python Overpass API which utilizes the OpenStreetMap database. We use tags ("ways", bridge", "tunnel") established in the database. Given GPS data, a check for the aforementioned tags is performed, resulting in true or false. This result is compared with the output the LMM generates. If there is a way (trafficable street in general) and a bridge or tunnel tag, the result is true. Note that the Sanity Check consists of two queries. One query checks for a 'way' tag to verify a trafficable street for vehicles. The other query checks for the 'bridge' or 'tunnel' tag. The result of both queries is taken into considerration for the final output the user of our application gets.

### Accuracy of the Sanity Check

The Sanity Check is dependent on the provided tags of the used API. 
The GPS data provided is the exact position where the images have been captured. Usually the relevant scene is not at this exact coordinate but further in front of the viewer's perspective. To cover this issue, we use a radius to check for relevant tags near the coordinates.
This radius is helpful, but also has its downsides. Determining a suitable value for this radius is crucial for the Sanity Check. 
Since the Sanity Check consists of two queries, two radius values are needed. 
The radius for the 'way' tag should not be too high since the image usually would be captured on or next to a street. Choosing a lower value ensures that no other 'way' tag is wrongly returned.
For the 'bridge' or 'tunnel' tag, the radius should be high enough to compensate the distance a user has to the scene they want to capture and have it analyzed.

So ultimately, the accuracy of the Sanity Check is the result of available tags from the database and fitting radius values for the queries used for the Overpass API.




### Abnormalities

Abnormalities may arise when there is no tag for the object depicted or the tags are not correctly implemented. For example, a tunnel could be only tagged as such in the middle, but not in the entrance points of it. Ultimately, the Sanity Check is limted to the OpenStreetMap database and its shortcomings.


### Performance of the Sanity Check

The Sanity Check usually performs well within a radius of 100 meters. It also performs well in high maintained areas where a lot of data has been added to the underlying database.


The Sanity Check verifies the GPS data for a captured image and simply returns true, if a way and a bridge or tunnel tag is found, or else returns false, if no such tags exists.
For the purpose of our application this implementation of the Sanity Check works well and can be used to verifiy the output an artifical intelligence system generates.


## Testing Large Language Models

Having engineered a fitting prompt and implemented the Sanity Check, we decided to test the large language models we redeemed most useful for our cause. The LLM we decided to potentially use are the following: GPT-4o, Gemini Pro, Gemini Flash, Claude, LLaVa and FIRELlaVa.

We attempted to find out the accuracy and time needed to calculate the result of the different models. 
These values are specific for our traffic analysis application. The results show how well the prompt performs with the different models to determine the usefulness for our app.

The accuracy we aquired through our testings is our base value for the overall accuracy that will be displayed to the user of the app. Hence it will be called 'historical accuracy'.

The historical accuracy will be altered depending on the Sanity Check result and the output of the choosen model. The resulting actual accuracy will help the user to estimate the trustworthiness of our application output whereas the historical accuracy will help the user to estimate the truthworthiness of the LLM in itself.

### Testing conditions

So now that we have established the need for an historical accuracy value, we need the means to calculate such a value. 
For this purpose we created testing data consisting of 50 images of bridges and tunnels. Before testing each LLM with the images and engineered prompt, the images were labeled with 0 and 1, whereas 0 stands for not underridable and 1 stands for underridable.
Afterwards each LMM has been given each image and the prompt to generate an output. 
To ensure all models only use informationen from the provided image and the prompt, we have given the files generic file names like "File1" etc., otherwise the LLM would in some cases extract information from the file name. 

### Metric for accuracy

We used the mean as the metric to determine the historical accuracy. So the historical accuracy is calculated like this:
```
historical accuracy = base set value / (true positive + true negative)
```

The base set value is the sum of all images, hence the value is 50.
True positive means the model has correctly identified if the depicted object is underridable.
True negative means the model has correctly identified if the depicted object is not underridable.

### Table for accuracy and latency of the LMMs
    
|     LMM      | accuracy |  latency   |
| :----------: | :------: | :--------: |
|    GPT-4o    |   94%    | 13 seconds |
|    Claude    |   90%    | 5 seconds  |
|  Gemini Pro  |   84%    | 10 seoncds |
| Gemini Flash |   60%    | 7 seconds  |
|    LLaVA     |   68%    | 30 seconds |
|  FireLLaVA   |   80%    | 10 seconds |



This table shows the result of the testing for all large language models. The latency of all models, except the local model, LLaVa, is within a similar timeframe, hence the model with the highest accuracy is best suited for the traffic analysis application. In the context of our app, GPT-4o bas proven to be the best suited large language model.
