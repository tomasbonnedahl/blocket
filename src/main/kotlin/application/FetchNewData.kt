package application

import DirtyFactory

class FetchNewData {
    companion object {
        fun run(fetchAndWriteConfiguration: FetchAndWriteConfiguration) {
            FetchAndWriteRunner.run(
                // TODO: Needs factory interface or something else (wrong dependency)
                DirtyFactory.newFetcher(fetchAndWriteConfiguration.fetchConfiguration),
                DirtyFactory.newWriter(fetchAndWriteConfiguration.writeConfiguration)
            )
        }
    }
}