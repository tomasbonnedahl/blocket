package application

import fetch.Fetcher
import write.Writer

class FetchAndWriteRunner {
    companion object {
        fun run(
            fetcher: Fetcher,
            writer: Writer
        ) {
            fetcher.fetch().forEach { car ->
                writer.write(car)
            }
        }
    }
}