package com.milvus.milvus;

import com.alibaba.fastjson.JSONObject;
import io.milvus.v2.client.MilvusClientV2;
import io.milvus.v2.common.DataType;
import io.milvus.v2.common.IndexParam;
import io.milvus.v2.service.collection.request.AddFieldReq;
import io.milvus.v2.service.collection.request.CreateCollectionReq;
import io.milvus.v2.service.collection.request.DropCollectionReq;
import io.milvus.v2.service.vector.request.InsertReq;
import io.milvus.v2.service.vector.request.SearchReq;
import io.milvus.v2.service.vector.response.SearchResp;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MilvusController {

    final MilvusClientV2 client;


    @PostMapping("/create")
    public void create() {
        CreateCollectionReq.CollectionSchema collectionSchema = client.createSchema();
        collectionSchema.addField(AddFieldReq.builder().fieldName("id").dataType(DataType.Int64).isPrimaryKey(Boolean.TRUE).autoID(Boolean.FALSE).description("id").build());
        collectionSchema.addField(AddFieldReq.builder().fieldName("vector").dataType(DataType.FloatVector).dimension(5).build());

        IndexParam indexParam = IndexParam.builder()
                .fieldName("vector")
                .metricType(IndexParam.MetricType.COSINE)
                .build();
        CreateCollectionReq createCollectionReq = CreateCollectionReq.builder()
                .collectionName("hey")
                .collectionSchema(collectionSchema)
                .indexParams(Collections.singletonList(indexParam))
                .build();
        client.createCollection(createCollectionReq);
    }

    @DeleteMapping("/delete")
    public void delete() {
        DropCollectionReq dropCollectionReq = DropCollectionReq.builder()
                .collectionName("hey")
                .build();
        client.dropCollection(dropCollectionReq);
    }

    @PostMapping("/insert")
    public void insert() {
        List<JSONObject> data = new ArrayList<>();

        JSONObject dict1 = new JSONObject();
        List<Float> vectorArray1 = new ArrayList<>();
        vectorArray1.add(0.37417449965222693f);
        vectorArray1.add(-0.9401784221711342f);
        vectorArray1.add(0.9197526367693833f);
        vectorArray1.add(0.49519396415367245f);
        vectorArray1.add(-0.558567588166478f);

        dict1.put("id", 1L);
        dict1.put("vector", vectorArray1);

        JSONObject dict2 = new JSONObject();
        List<Float> vectorArray2 = new ArrayList<>();
        vectorArray2.add(0.46949086179692356f);
        vectorArray2.add(-0.533609076732849f);
        vectorArray2.add(-0.8344432775467099f);
        vectorArray2.add(0.9797361846081416f);
        vectorArray2.add(0.6294256393761057f);

        dict2.put("id", 2L);
        dict2.put("vector", vectorArray2);

        data.add(dict1);
        data.add(dict2);

        InsertReq insertReq = InsertReq.builder()
                .collectionName("hey")
                .data(data)
                .build();
        client.insert(insertReq);
    }

    @GetMapping("/search")
    public void search() {
        List<Float> vectorList = new ArrayList<>();
        vectorList.add(1.0f);
        vectorList.add(2.0f);
        vectorList.add(3.0f);
        vectorList.add(5.0f);
        vectorList.add(4.0f);

        SearchReq searchReq = SearchReq.builder()
                .collectionName("hey")
                .data(Collections.singletonList(vectorList))
                .topK(1)
                .build();
        SearchResp searchResp = client.search(searchReq);
        System.out.println("==================================");
        System.out.println(searchResp);
    }
}
