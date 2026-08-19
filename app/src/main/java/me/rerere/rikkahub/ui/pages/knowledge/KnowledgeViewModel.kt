package me.rerere.rikkahub.ui.pages.knowledge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.rerere.rikkahub.data.model.KnowledgeItem
import me.rerere.rikkahub.data.repository.KnowledgeRepository

class KnowledgeViewModel(
    private val repository: KnowledgeRepository
) : ViewModel() {
    
    private val _knowledge = MutableStateFlow<List<KnowledgeItem>>(emptyList())
    val knowledge: StateFlow<List<KnowledgeItem>> = _knowledge.asStateFlow()
    
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    fun loadKnowledge(assistantId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _knowledge.value = repository.getKnowledge(assistantId)
                _categories.value = repository.getCategories(assistantId)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addKnowledge(title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            repository.addKnowledge(null, title, content, category, tags)
            loadKnowledge(KnowledgeRepository.GLOBAL_KNOWLEDGE_ID)
        }
    }
    
    fun updateKnowledge(id: Int, title: String, content: String, category: String, tags: String) {
        viewModelScope.launch {
            repository.updateKnowledge(id, title, content, category, tags)
            loadKnowledge(KnowledgeRepository.GLOBAL_KNOWLEDGE_ID)
        }
    }
    
    fun deleteKnowledge(id: Int) {
        viewModelScope.launch {
            repository.deleteKnowledge(id)
            loadKnowledge(KnowledgeRepository.GLOBAL_KNOWLEDGE_ID)
        }
    }
}
