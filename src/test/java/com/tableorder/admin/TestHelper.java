package com.tableorder.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tableorder.admin.domain.AdminAccount;
import com.tableorder.admin.domain.Store;
import com.tableorder.admin.repository.AdminAccountRepository;
import com.tableorder.admin.repository.StoreRepository;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class TestHelper {

    public static Store createStore(StoreRepository repo) {
        try {
            var constructor = Store.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Store store = constructor.newInstance();
            var nameField = Store.class.getDeclaredField("name");
            nameField.setAccessible(true);
            nameField.set(store, "테스트매장");
            var pinField = Store.class.getDeclaredField("masterPin");
            pinField.setAccessible(true);
            pinField.set(store, "000000");
            return repo.save(store);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void createAdmin(AdminAccountRepository repo, PasswordEncoder encoder, Long storeId) {
        try {
            var constructor = AdminAccount.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            AdminAccount admin = constructor.newInstance();
            setField(admin, "storeId", storeId);
            setField(admin, "username", "admin");
            setField(admin, "passwordHash", encoder.encode("admin1234"));
            repo.save(admin);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String login(MockMvc mockMvc, ObjectMapper mapper, Long storeId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"storeId": %d, "username": "admin", "password": "admin1234"}
                            """.formatted(storeId)))
                .andReturn();
        return mapper.readTree(result.getResponse().getContentAsString())
                .get("accessToken").asText();
    }

    private static void setField(Object obj, String fieldName, Object value) throws Exception {
        var field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
