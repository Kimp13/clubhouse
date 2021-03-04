package com.example.clubhouse.interfaces

/**
 * Интерфейс, обрабатывающий клик на карточку контакта
 */
interface ContactCardClickListener {
    /**
     * Обработчик клика
     *
     * @param id Идентификатор контакта
     */
    fun onCardClick(id: Int)
}