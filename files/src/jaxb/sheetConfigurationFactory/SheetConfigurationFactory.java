package jaxb.sheetConfigurationFactory;

import cell.Cell;
import common.enums.SheetOption;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import jaxb.dto.SheetConfiguration;
import jaxb.generated.STLCell;
import jaxb.generated.STLRange;
import jaxb.generated.STLSheet;
import position.PositionFactory;
import position.interfaces.IPosition;
import range.CellRange;
import range.IRange;
import store.SetContextStore;
import store.TypedContextStore;

import java.io.File;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SheetConfigurationFactory {
    protected STLSheet stlSheet;

    public SheetConfiguration create(String filePath) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(STLSheet.class);
        File xmlFile = new File(filePath);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        stlSheet = (STLSheet) unmarshaller.unmarshal(xmlFile);
        return createSheet();
    }

    protected abstract SheetConfiguration createSheet();

    protected abstract void createCell(STLCell stlCell, List<Cell> cells);

    protected void safeCreateCell(STLCell stlCell, List<Cell> cells) {
        safeExecute(() -> createCell(stlCell, cells), cells, null);
    }

    public static SheetConfigurationFactory newInstance(SheetOption option) {
        return createFactoryMap().get(option);
    }

    private static Map<SheetOption, SheetConfigurationFactory> createFactoryMap() {
        return new EnumMap<>(Map.of(
                SheetOption.NEW, new NewSheetConfigurationFactory(),
                SheetOption.LOAD, new ExistingSheetConfigurationFactory()
        ));
    }

    protected void safeExecute(Runnable runnable,  List<Cell> cells, Map<String, IRange> ranges) {
        try {
            SetContextStore.getCellSetStore().setContext(cells);
            TypedContextStore.getRangesStore().setContext(ranges);
            runnable.run();
        } finally {
            SetContextStore.getCellSetStore().clearContext();
            TypedContextStore.getRangesStore().clearContext();
        }
    }

    protected Map<String, IRange> getRanges(List<STLRange> ranges) {
        return ranges.stream()
                .collect(Collectors.toMap(
                        STLRange::getName,
                        this::STLRangeToCellRange
                ));
    }

    private IRange STLRangeToCellRange(STLRange range) {
        IPosition from = PositionFactory.create(range.getSTLBoundaries().getFrom());
        IPosition to = PositionFactory.create(range.getSTLBoundaries().getTo());
        return CellRange.of(range.getName(), from, to);
    }
}
