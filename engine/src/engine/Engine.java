package engine;

import cell.Cell;
import cell.dto.CellBasicDetails;
import cell.dto.CellDetails;
import cell.dto.UpdateCellDto;
import engine.semaphore.ISemaphoreTask;
import position.interfaces.IPosition;
import sheet.interfaces.ISheet;

import java.util.Map;
import java.util.concurrent.Semaphore;

public final class Engine implements IEngine {
    private final ISheet sheet;
    private final Semaphore semaphore = new Semaphore(1);

    public Engine(ISheet sheet) { this.sheet = sheet; }

    @Override
    public void updateCell(UpdateCellDto updateCellDto) {
        safeExecute(() -> { sheet.updateCell(updateCellDto.position(), updateCellDto.newOriginalValue()); return null; });
    }

    @Override
    public Map<IPosition, Cell> getHistory(int version) { return safeExecute(() -> sheet.getPastVersion(version)); }

    @Override
    public Map<Integer, Integer> getUpdateCountList() { return safeExecute(sheet::getUpdate2VersionCount); }

    @Override
    public String getSheetName() { return safeExecute(sheet::getName); }

    @Override
    public CellBasicDetails getCellBasicDetails(IPosition position) {
        return safeExecute(() -> sheet.getCellBasicDetails(position));
    }

    @Override
    public CellDetails getCellDetails(IPosition position) {
        return safeExecute(() -> sheet.getCellDetails(position));
    }

    @Override
    public Map<IPosition, Cell> getWhatIfCells(String originalValue, IPosition position) {
        return safeExecute(() -> sheet.getWhatIfCells(originalValue, position));
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
