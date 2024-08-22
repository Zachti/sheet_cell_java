package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import engine.semaphore.ISemaphoreTask;
import position.interfaces.IPosition;
import range.CellRange;
import sheet.interfaces.ISheet;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

public final class Engine implements IEngine {
    private final ISheet sheet;
    private final Semaphore semaphore = new Semaphore(1);

    public Engine(ISheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void updateCell(UpdateCellDto updateCellDto) {
        safeExecute(() -> {
            sheet.updateCell(updateCellDto);
            return null;
        });
    }

    @Override
    public Map<IPosition, Cell> getHistory(int version) {
        return safeExecute(() -> sheet.getPastVersion(version));
    }

    @Override
    public Map<Integer, Integer> getUpdateCountList() {
        return safeExecute(sheet::getUpdate2VersionCount);
    }

    @Override
    public String getSheetName() {
        return safeExecute(sheet::getName);
    }

    @Override
    public CellBasicDetails getCellBasicDetails(IPosition position) {
        return safeExecute(() -> sheet.getCellBasicDetails(position));
    }

    @Override
    public CellDetails getCellDetails(IPosition position) {
        return safeExecute(() -> sheet.getCellDetails(position));
    }

    @Override
    public void addRange(CellRange range) {
        safeExecute(() -> {
            sheet.addRange(range);
            return null;
        });
    }

    @Override
    public void removeRange(CellRange range) {
        safeExecute(() -> {
            sheet.removeRange(range);
            return null;
        });
    }

    @Override
    public List<CellRange> getRanges() {
        return safeExecute(sheet::getRanges);
    }

    @Override
    public List<Cell> viewCellsInRange(CellRange range) {
        return safeExecute(() -> sheet.viewCellsInRange(range));
    }

    @Override
    public List<Integer> getRowsByFilter(CellRange range, List<Object> selectedValues) {
        return safeExecute(() -> sheet.getRowsByFilter(range, selectedValues));
    }

    @Override
    public List<Integer> sortRowsInRange(CellRange range, List<Character> columns, boolean ascending) {
        return safeExecute(() -> sheet.sortRowsInRange(range, columns, ascending));
    }

    @Override
    public Map<IPosition, Cell> getWhatIfCells(List<UpdateCellDto> updateCellDtos) {
        return safeExecute(() -> sheet.getWhatIfCells(updateCellDtos));
    }

    private <T> T safeExecute(ISemaphoreTask<T> task) {
        try {
            semaphore.acquire();
            return task.execute();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted", e);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            semaphore.release();
        }
    }
}

