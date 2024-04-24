package com.sample.homework;

import java.util.*;
import java.io.*;

public class HomeworkTwo {
	static Map<String, ArrayList<String>> table;
	static int rows;
	static int cols;
	static Scanner sc;

	static String fileName = "src/main/resources/homework_two.txt";
	static String DELIMITTER = "  |  ";
	static String NEW_LINE = "\n";
	static String COMMA = ",";

	static String menuError = "Input is not on the menu";
	static String inputError = "Wrong input format";
	static String newValueError = "New value must have 3 characters";
	static String editTypeError = "Must only edit a 'key' or a 'value' ";
	static String indexError = "Index out of bounds";
	static String savingError = "Encountered an error while saving the table";
	static String readingError = "Error occurred while reading file";

	public static void main(String[] args) {
		sc = new Scanner(System.in);
		
		start();

		int choice = -1;
		while(choice != 0){
			System.out.print(
				"\n---------- M E N U ----------\n"+
				"[1] Search\n"+
				"[2] Edit\n"+
				"[3] Print Table\n"+
				"[4] Sort\n"+
				"[5] Add Column\n"+
				"[6] Reset\n"+
				"[0] Exit\n"+
				"Enter choice: ");
			choice = Integer.parseInt(sc.nextLine());

			switch(choice) {
				case 0:
					saveToFile();
					break;
				case 1:
					System.out.print("Search: ");
					String chr = sc.nextLine();
					search(chr);
					break;
				case 2:
					try {
						System.out.print("Edit(ROWxCOL - key and/or value): ");
						String[] toEdit = sc.nextLine().split("-");
						String[] editDimension = toEdit[0].replace(" ","").split("x|,");
						int editRow = Integer.parseInt(editDimension[0]);
						int editCol = Integer.parseInt(editDimension[1]);
						String[] editType = toEdit[1].replace(" ", "").toUpperCase().split("AND|&|,");

						System.out.print("New Value: ");
						String[] editValue = sc.nextLine().replace(" ", "").split(COMMA);
						System.out.println("row: "+editRow+" col: "+editCol+" type: "+Arrays.asList(editType)+" value: "+Arrays.asList(editValue));
						edit(editRow, editCol, editType, editValue);
					} catch (Exception e){
						System.out.println(inputError);
					}
					
					break;
				case 3:
					displayTable();
					break;
				case 4:
					System.out.print("Sort order: ");
					String order = sc.nextLine().trim().toUpperCase();
					sort(order);
					break;
				case 5:
					try {
						System.out.print("Add on Row: ");
						int row = Integer.parseInt(sc.nextLine().trim());

						System.out.print("New Value: ");
						String[] value = sc.nextLine().replace(" ", "").split(COMMA);
						addColumn(row, value);
					} catch (Exception e) {
						System.out.println(inputError);
					}
					
					break;
				case 6:
					reset();
					break;
				default:
					System.out.println(menuError);
					break;
			}
		}
	}

	public static Map<String, ArrayList<String>> generateTable(int rows, int cols) {
		Map<String, ArrayList<String>> table = new HashMap<>();

		for(int i=0; i<rows; i++) {
			for(int j=0; j<cols; j++) {
				ArrayList<String> input = new ArrayList<String>();
				input.add(randomizeString());
				input.add(randomizeString());

				table.put(i+COMMA+j, input);
			}
		}

		return table;
	}

	public static String randomizeString() {
		Random random = new Random();
		String value = "";
		for(int i=0; i<3; i++) {
			value += (char) (random.nextInt(93) + 33);

		}
		return value;
	}

	public static void displayTable() {
		System.out.println("\n---------- T A B L E ----------");
		for(int i=0; i<rows; i++){
			for(int j=0; j<cols; j++) {
				if(isTableValueExist(i,j)){
					ArrayList<String> input = table.get(i+COMMA+j);
					System.out.print(input.get(0)+COMMA+input.get(1)+DELIMITTER);
				} else {
					System.out.print("       "+DELIMITTER);
				}
			}
			System.out.println();
		}
		System.out.println("-------------------------------");
	}

	public static void search(String chr) {
		for(Map.Entry<String, ArrayList<String>> entry : table.entrySet()) {
			ArrayList<String> input = entry.getValue();
			if((input.get(0)+input.get(1)).contains(chr)){
				int keyOccurence = getOccurence(chr, input.get(0));
				int valueOccurence = getOccurence(chr, input.get(1));

				System.out.print("["+entry.getKey()+"] - ");
				if(keyOccurence > 0) {
					System.out.print("Found on Key Field: "+keyOccurence+"; ");
				}
				if(valueOccurence > 0) {
					System.out.print("Found on Value Field: "+valueOccurence+"; ");
				}

				System.out.println();
			}
		}
	}

	public static int getOccurence(String chr, String input) {
		return input.length() - input.replace(chr, "").length();
	}

	public static void edit(int row, int col, String[] type, String[] value) {
		String KEY = "KEY";
		String VALUE = "VALUE";

		if(value.length != type.length || value.length > 2 || type.length > 2){
			System.out.println(inputError);
		} else if (row >= rows || col >= cols) {
			System.out.println(indexError);
		} else {
			ArrayList<String> input = table.get(row+COMMA+col);
			for(int i=0; i<type.length; i++) {
				if(value[i].length() != 3) {
					System.out.println(newValueError);
				} else if (!KEY.equals(type[i]) && !VALUE.equals(type[i])){
					System.out.println(editTypeError);
				} else {
					if(KEY.equals(type[i])) {
						input.set(0, value[i]);
					} else {
						input.set(1, value[i]);
					}
				}
			}
			table.put(row+COMMA+col, input);
		}
	}

	public static void sort(String order) {
		Set<String> ASC = Set.of("ASC", "ASCENDING");
		Set<String> DSC = Set.of("DESC", "DESCENDING");

		if(!ASC.contains(order) && !DSC.contains(order)){
			System.out.println(inputError);
		} else {
			for(int i=0; i<rows; i++) {
				ArrayList<String> rowValues = new ArrayList<>();
				for(int j=0; j<cols; j++){
					if(isTableValueExist(i,j)){
						String value = table.get(i+COMMA+j).get(0)+table.get(i+COMMA+j).get(1);
						rowValues.add(value);
					}
				}
				if(ASC.contains(order)) {
					Collections.sort(rowValues);
				} else {
					Collections.sort(rowValues, Collections.reverseOrder());
				}
				for(int j=0; j<cols; j++) {
					if(isTableValueExist(i,j)) {
						ArrayList<String> temp = new ArrayList<>(
							Arrays.asList(rowValues.get(j).substring(0,3), rowValues.get(j).substring(3,6)));
						table.put(i+COMMA+j, temp);
					}
				}
			}
		}
	}
	public static boolean isTableValueExist(int row, int col) {
		if(table.get(row+COMMA+col) != null) {
			return true;
		}
		return false;
	}

	public static void addColumn(int row, String[] value) {
		ArrayList<String> newValue = new ArrayList<>(Arrays.asList(value));

		if(newValue.get(0).length() != 3 || newValue.get(1).length() != 3) {
			System.out.println(newValueError);
		} else if( row >= rows) {
			System.out.println(indexError);
		} else {
			int j = 0;
			while(j<cols) {
				if(table.get(row+COMMA+j) == null){
					break;
				}
				j++;
			}
			table.put(row+COMMA+j, newValue);
			if(j == cols){
				cols++;
			}
			
		}

	}

	public static void reset() {
		System.out.print("User Input: ");
		String dimension = sc.nextLine();
		
		try {
			rows = Integer.parseInt(dimension.split("x|,")[0]);
			cols = Integer.parseInt(dimension.split("x|,")[1]);

			table = generateTable(rows, cols);
		} catch(Exception e) {
			System.out.println(inputError);
		}
		
	}

	public static void saveToFile() {
		try {
			FileOutputStream file =  new FileOutputStream(fileName);
	      	OutputStreamWriter writer = new OutputStreamWriter(file, "UTF-8");
	      	for(int i=0; i<rows; i++) {
	      		for(int j=0; j<cols; j++){
	      			if(isTableValueExist(i,j)){
	      				ArrayList<String> input = table.get(i+COMMA+j);
						writer.write(input.get(0)+COMMA+input.get(1)+DELIMITTER);
					}
	      		}
	      		writer.write(NEW_LINE);
	      	}
	      	writer.flush();
	      	writer.close();
	    } catch (IOException e) {
	      	System.out.println(savingError);
	    }
	}

	public static void start() {
		try{
			FileInputStream file = new FileInputStream(fileName);
			InputStreamReader reader = new InputStreamReader(file);

			int ch;
			int count = 0;
			StringBuilder content = new StringBuilder();
			while((ch = reader.read()) > 0) {
				content.append((char) ch);
				count++;
			}
			if(count == 0){
				reset();
			} else {
				parseContent(content.toString());
			}
		} catch (FileNotFoundException e) {
            reset();
        } catch (IOException e) {
            System.out.println(readingError);
        }
	}

	public static void parseContent(String content) {
		String[] lines = content.split(NEW_LINE);
		table = new HashMap<>();
		rows = lines.length;
		cols = 0;

		for(int i=0; i<lines.length; i++) {
			String[] value = lines[i].split("  \\|  ");
			if(cols < value.length){
				cols = value.length;
			}
			for(int j=0; j<value.length; j++){
				ArrayList<String> temp = new ArrayList<>();
				temp.add(value[j].substring(0,3));
				temp.add(value[j].substring(4,7));
				table.put(i+COMMA+j, temp);
			}
		}
	}
}