import React from 'react';
import HeaderView from './HeaderView.jsx';

export default class Header extends React.Component {

    constructor(props) {
        super(props);
        this.getMenu = this.getMenu.bind(this);
        this.changeSound = this.changeSound.bind(this)
    }

    changeSound(){
        this.context.functions.changeOptions({sound: !this.context.options.sound});
    }


    getMenu() {
        let soundState = (this.context.options.sound) ? 'online' : 'offline';
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
            },
            {
                name: 'Sound: ',
                action: this.changeSound,
                customObject: <span className={"circle " + soundState} />
            }
        ]
    }

    render() {
        return (
            <HeaderView menu={this.getMenu()} />
        );
    }
}

Header.contextTypes = {
    functions: React.PropTypes.object,
    options: React.PropTypes.object
};