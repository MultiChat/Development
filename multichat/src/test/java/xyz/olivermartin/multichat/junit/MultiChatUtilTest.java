package xyz.olivermartin.multichat.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import xyz.olivermartin.multichat.common.MultiChatUtil;
import xyz.olivermartin.multichat.common.TranslateMode;

public class MultiChatUtilTest {

	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(MultiChatUtilTest.class);
		for (Failure failure : result.getFailures()) {
			System.out.println(failure.toString());
		}
	}

	@Test
	public void shouldTranslateColourCodesCorrectly() {

		String rawMessage = "&r&aHello &kthere! &6&lthis &ois &ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!";

		// ALL
		assertEquals("All colour codes should be translated appropriately",
				"§r§aHello §kthere! §6§lthis §ois §ma §nmessage! §r§x§a§b§c§d§e§fRGB §r§x§a§b§c§d§e§ftoo§r§x§a§b§c§d§e§f!",
				MultiChatUtil.translateColourCodes(rawMessage));

		// ALL #2
		assertEquals("All colour codes should be translated appropriately",
				"§r§aHello §kthere! §6§lthis §ois §ma §nmessage! §r§x§a§b§c§d§e§fRGB §r§x§a§b§c§d§e§ftoo§r§x§a§b§c§d§e§f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.ALL));

		// SIMPLE
		assertEquals("All colour codes should be translated appropriately",
				"§r§aHello §kthere! §6§lthis §ois §ma §nmessage! &#ABCDEFRGB &xAbCdEftoo&x§a§b§c§d§e§f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.SIMPLE));

		// SIMPLE COLOUR
		assertEquals("All colour codes should be translated appropriately",
				"§r§aHello &kthere! §6&lthis &ois &ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x§a§b§c§d§e§f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.COLOUR_SIMPLE));

		// ALL COLOUR
		assertEquals("All colour codes should be translated appropriately",
				"§r§aHello &kthere! §6&lthis &ois &ma &nmessage! §r§x§a§b§c§d§e§fRGB §r§x§a§b§c§d§e§ftoo§r§x§a§b§c§d§e§f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.COLOUR_ALL));

		// FORMAT UNDERLINE
		assertEquals("All colour codes should be translated appropriately",
				"§r&aHello &kthere! &6&lthis &ois &ma §nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.FORMAT_UNDERLINE));

		// FORMAT ITALIC
		assertEquals("All colour codes should be translated appropriately",
				"§r&aHello &kthere! &6&lthis §ois &ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.FORMAT_ITALIC));

		// FORMAT BOLD
		assertEquals("All colour codes should be translated appropriately",
				"§r&aHello &kthere! &6§lthis &ois &ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.FORMAT_BOLD));

		// FORMAT STRIKE
		assertEquals("All colour codes should be translated appropriately",
				"§r&aHello &kthere! &6&lthis &ois §ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.FORMAT_STRIKE));

		// FORMAT OBFUSCATED
		assertEquals("All colour codes should be translated appropriately",
				"§r&aHello §kthere! &6&lthis &ois &ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.FORMAT_OBFUSCATED));

		// FORMAT ALL
		assertEquals("All colour codes should be translated appropriately",
				"§r&aHello §kthere! &6§lthis §ois §ma §nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!",
				MultiChatUtil.translateColourCodes(rawMessage, TranslateMode.FORMAT_ALL));

	}

	@Test
	public void shouldNotChangeWithMultipleTranslations() {

		String rawMessage = "&r&aHello &kthere! &6&lthis &ois &ma &nmessage! &#ABCDEFRGB &xAbCdEftoo&x&a&b&c&d&e&f!";

		String translated1 = MultiChatUtil.translateColourCodes(rawMessage);

		String translated2 = MultiChatUtil.translateColourCodes(translated1);

		assertEquals("Resulting translations should be the same after multiple parses",
				translated1,
				translated2);

	}

}
