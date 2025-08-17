import net.r4mble.types.Card;
import org.junit.jupiter.api.Test;

public class CardInitTest {
    @Test
    void testAPI() {
        Card card = new Card("59af8cac-052b-4eef-8a88-fb3827565aa4", "EvZQcI+xHEPA2N4J63da3UBgOcvCLPEP");
        System.out.println(card.getAuthStatus());
        System.out.println(card.getBalance());
        System.out.println(card.getName());
    }
}
