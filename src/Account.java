import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Account implements BankAccountFunctions{

    Customer customer = new Customer();
    private double balance = 0;
    private Account currentAccount;
    private List<Account> accounts;

    public Account(String personNumber, String name, int age) {
        customer.setPersonNumber(personNumber);
        customer.setName(name);
        customer.setAge(age);
    }

    public double setBalance(double balance){
        return this.balance += balance;
    }
    @Override
    public double getBalance() {
        if (currentAccount != null) {
            double balance = currentAccount.getBalance();
            System.out.println("Current balance: " + balance + " kr");
            return balance;
        } else {
            System.out.println("No active account selected");
            return 0.0;
        }
    }

    public void deposit() {
        Scanner sc = new Scanner(System.in);

        // Kontrollera om det finns några konton
        if (accounts == null) {
            System.out.println("No active account selected");
            return;
        }

        System.out.print("Enter amount to deposit: ");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Amount must be positive");
            return;
        }

        currentAccount.setBalance(amount);
        System.out.println("New balance: " + currentAccount.getBalance() + " kr");
    }
    public void withdraw() {
        Scanner sc = new Scanner(System.in);

        //om de finns ett aktivt konto,
        if (currentAccount == null) {
            System.out.println("No active account selected");
            return;
        }

        System.out.print("Enter amount to withdraw: ");
        double amount = sc.nextDouble();

        if (amount <= 0) { //// Kollar att summan är större än noll (d är en säkerhetskontroll, går ej å ta ut om d negativt)
            System.out.println("Amount must be positive");
            return;
        }

        if (amount > currentAccount.getBalance()) { //om d finns tillräckligt med para
            System.out.println("Insufficient funds");
            return;
        }

        currentAccount.setBalance(-amount);  // Använder negativt belopp för uttag
        System.out.println("Successfully withdrew " + amount + " kr");
        System.out.println("New balance: " + currentAccount.getBalance() + " kr");
    }


    public void writeToFile(String name, int age, String pnr, double balance) {
        File file = new File("customers.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(name + ", " + age + ", " + pnr + ", " + balance + " kr");
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void payBill() {
        Scanner sc = new Scanner(System.in);

        if (currentAccount ==null) { //om personen e inloggad å de finns ett aktivt konto,
            System.out.println("No active account selected");
            return;
        }
        System.out.println("\n--- Bill Payment ---");
        System.out.println("Enter recipient name: ");
        String recipient = sc.nextLine();

        System.out.print("Enter bill amount: ");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Amount most be positive ");
            return;
        }
        if (amount > currentAccount.getBalance()) { // OM de finns pengar på kontot
            System.out.println("Your balance is too low for this payment");
            System.out.println("Current balance: " + currentAccount.getBalance() + " kr");
            return;
        }
        
    }

    public void logIn() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter person number: ");
        String pnr = sc.next();

        try {
            File file = new File("customers.txt"); //öppnar filen me konto
            if (!file.exists()) {
                System.out.println("No accounts found");
                return;
            }

            Scanner fileScanner = new Scanner(file);
            boolean accountFound = false;

            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                //delar upp raderna så de blir name,age,pnr,balance
                String[] parts = line.split(", ");
                if (parts.length >= 3 && parts[2].equals(pnr)) { //om d matchar pnr alltså,
                    String name = parts[0]; //hämtar all kontoinfo
                    int age = Integer.parseInt(parts[1]);
                    double balance = Double.parseDouble(parts[3].replace(" kr", ""));

                    // Skapar konto och lägg till i listan
                    Account account = new Account(pnr, name, age);
                    account.setBalance(balance);
                    this.currentAccount = account;
                    this.accounts.add(account);

                    System.out.println("Successfully logged in!");
                    accountPrompt();
                    getAccountInputChoice();
                    return;
                }
            }
            fileScanner.close();
            System.out.println("Account not found");
        } catch (FileNotFoundException e) {
            System.out.println("Error reading customer file");
        }
    }

    public void accountPrompt(){
        System.out.println("----------------------------------------");
        System.out.println("Choose what you would like to do: ");
        System.out.println( "1: Check balance\n" +
                "2: Deposit money\n" +
                "3: Whitdraw money from account\n" +
                "4: Pay bill\n" +
                "5: Exit ");
        //Lägga till ett val för att skapa ett subkonto?
    }
    public void getAccountInputChoice(){
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();

        switch (choice) {
            case 1:
                getBalance();
                accountPrompt();
                getAccountInputChoice();
                break;
            case 2:
                deposit();
                accountPrompt();
                getAccountInputChoice();
                break;

            case 3:
                withdraw();
                accountPrompt();
                getAccountInputChoice();
                break;
            case 4:
                payBill();
                accountPrompt();
                getAccountInputChoice();
                break;
            case 5:
                System.out.println("Goodbye!");
                System.exit(0);

        }
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }
}