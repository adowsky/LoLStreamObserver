import React from 'react';
import HeaderView from './HeaderView.jsx';

export default class Header extends React.Component {

    constructor(props) {
        super(props);
        this.getMenu = this.getMenu.bind(this);
    }

    getChildContext() {
        return {
            menu: this.getMenu()
        };
    }


    getMenu() {
        return [
            {
                action: this.context.functions.showAdd,
                name: 'Add Entry'
            },
            {
                name: 'Force Refresh',
                action: this.context.functions.fetchData
            },
            {
                name: 'Purge Data',
                action: this.context.functions.purgeData
            }
        ]
    }

    render() {
        return (
            <HeaderView/>
        );
    }
}

Header.childContextTypes = {
    menu: React.PropTypes.array
};
Header.contextTypes = {
    functions: React.PropTypes.object
};