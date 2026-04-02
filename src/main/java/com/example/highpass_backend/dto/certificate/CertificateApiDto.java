package com.example.highpass_backend.dto.certificate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificateApiDto {
    private Response response;

    @Getter @Setter
    @JsonDeserialize(using = BodyDeserializer.class)
    public static class Response {
        private Body body;
    }

    static class BodyDeserializer extends JsonDeserializer<Body> {
        @Override
        public Body deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            if (p.currentToken().isScalarValue()) {
                return null;
            }
            return ctx.readValue(p, Body.class);
        }

    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Body {
        private Items items;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items {
        // 🌟 데이터가 1개든 여러 개든 무조건 List로 받아주는 마법의 설정
        @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        private List<Item> item;
    }

    @Getter @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String description;
        private String docregstartdt;
        private String docregenddt;
        private String docexamdt;
        private String docpassdt;
        private String pracregstartdt;
        private String pracregenddt;
        private String pracexamstartdt;
        private String pracpassdt;
    }
}
