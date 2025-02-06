package recargapay.wallet.infra.cache;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;

public class PageSerializer extends StdSerializer<Page<?>> implements RedisSerializer<Page<?>> {

    public PageSerializer() {
        super((Class<Page<?>>) (Class<?>) Page.class);
    }

    @Override
    public byte[] serialize(Page<?> page) throws SerializationException {
        try {
            return new ObjectMapper().writeValueAsBytes(page);
        } catch (IOException e) {
            throw new SerializationException("Erro ao serializar a página", e);
        }
    }

    @Override
    public Page<?> deserialize(byte[] bytes) throws SerializationException {
        try {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(bytes, new TypeReference<Page<?>>() {});
        } catch (IOException e) {
            throw new SerializationException("Erro ao desserializar a página", e);
        }
    }


    @Override
    public void serialize(Page<?> page, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeFieldName("content");
        provider.defaultSerializeValue(page.getContent(), gen);
        gen.writeFieldName("pageable");
        gen.writeStartObject();
        gen.writeFieldName("sort");
        gen.writeStartObject();
        gen.writeBooleanField("sorted", page.getSort().isSorted());
        gen.writeBooleanField("unsorted", page.getSort().isUnsorted());
        gen.writeBooleanField("empty", page.getSort().isEmpty());
        gen.writeEndObject();

        gen.writeNumberField("offset", page.getPageable().getOffset());
        gen.writeNumberField("pageSize", page.getPageable().getPageSize());
        gen.writeNumberField("pageNumber", page.getPageable().getPageNumber());
        gen.writeBooleanField("unpaged", page.getPageable().isUnpaged());
        gen.writeBooleanField("paged", page.getPageable().isPaged());
        gen.writeEndObject();

        gen.writeNumberField("totalPages", page.getTotalPages());
        gen.writeNumberField("totalElements", page.getTotalElements());
        gen.writeBooleanField("last", page.isLast());
        gen.writeNumberField("size", page.getSize());
        gen.writeNumberField("number", page.getNumber());

        gen.writeFieldName("sort");
        gen.writeStartObject();
        gen.writeBooleanField("sorted", page.getSort().isSorted());
        gen.writeBooleanField("unsorted", page.getSort().isUnsorted());
        gen.writeBooleanField("empty", page.getSort().isEmpty());
        gen.writeEndObject();

        gen.writeNumberField("numberOfElements", page.getNumberOfElements());
        gen.writeBooleanField("first", page.isFirst());
        gen.writeBooleanField("empty", page.isEmpty());

        gen.writeEndObject();
    }


}
