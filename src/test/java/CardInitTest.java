import net.r4mble.types.Card;
import org.junit.jupiter.api.Test;

public class CardInitTest {
    @Test
    void testReverse() {
        Card card = new Card("yr", "te");
        System.out.println(card.getAuthStatus());
    }
}
