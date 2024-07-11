public class Main {
  public static String resolver = null;
  public static void main(String[] args) {
    // You can use print statements as follows for debugging, they'll be visible when running tests.
    System.out.println("Logs from your program will appear here!");

    if (args.length > 1 && args[0].equals("--resolver")) {
      resolver = args[1];
    }

    try (
              DnsServer dnsServer = Main.resolver == null ? new DnsServer(2053) : new DnsServer(2053, Main.resolver);
    ) {
      dnsServer.start();
    }
  }
}
