package cl.duoc.casasembrujadas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Comprueba que el contexto de Spring arranca con la configuración del proyecto.
 *
 * @author Dinko Ocampo
 */
@SpringBootTest(properties = {
		"DB_URL=jdbc:postgresql://localhost:5432/casas_embrujadas",
		"DB_USERNAME=prueba",
		"DB_PASSWORD=prueba",
		"JWT_SECRET=c2VjcmV0by1zb2xvLXBhcmEtcHJ1ZWJhcy1hdXRvbWF0aWNhcy0xMjM0NTY3ODkw"
})
class CasasEmbrujadasApiApplicationTests {

	@Test
	void contextLoads() {
	}
}