package jaxb.uiSheetFactory;

import cell.Cell;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jaxb.generated.STLCell;
import jaxb.generated.STLSheet;
import jaxb.generated.UiSheet;
import menu.enums.SheetOption;
import store.SetContextStore;

import java.io.File;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class SheetFactory {
    protected STLSheet sheet;

    public UiSheet create(String filePath) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(STLSheet.class);
        File xmlFile = new File(filePath);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        sheet = (STLSheet) unmarshaller.unmarshal(xmlFile);
        return createSheet();
    }

    protected abstract UiSheet createSheet();

    protected abstract void createCell(STLCell stlCell, LinkedList<Cell> cells);

    protected void safeCreateCell(STLCell stlCell, LinkedList<Cell> cells) {
        SetContextStore.getCellSetStore().setContext(cells);
        createCell(stlCell, cells);
        SetContextStore.getCellSetStore().clearContext();
    }

    public static SheetFactory newInstance(SheetOption option) {
        return createFactoryMap().get(option);
    }

    private static Map<SheetOption, SheetFactory> createFactoryMap() {
        return new EnumMap<>(Map.of(
                SheetOption.NEW, new NewSheetFactory(),
                SheetOption.LOAD, new ExistingSheetFactory()
        ));
    }
}
