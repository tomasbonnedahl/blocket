class WriteConfiguration(private val filters: List<ExclusionFilter> = emptyList()) {
    fun filters(): List<ExclusionFilter> {
        return filters
    }
}